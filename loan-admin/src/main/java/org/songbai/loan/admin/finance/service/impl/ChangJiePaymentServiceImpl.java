package org.songbai.loan.admin.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.finance.service.PaymentService;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.user.dao.UserBankCardDao;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.common.finance.PaySignUtil;
import org.songbai.loan.common.finance.RSA;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: wjl
 * @date: 2018/11/30 13:01
 * Description: 畅捷支付Service
 */
@Service("changJiePaymentService")
public class ChangJiePaymentServiceImpl implements PaymentService {
	private static final Logger log = LoggerFactory.getLogger(ChangJiePaymentServiceImpl.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private BasicPaymentService basicPaymentService;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private SpringProperties springProperties;
	@Autowired
	private ComFinanceService comFinanceService;

	public String charset = "UTF-8";
	public String signType = "RSA";

	@Override
	public List<Integer> validate(List<OrderModel> orderModels, Integer agencyId, Integer actorId) {

		//获取配置
		PlatformConfig config = getConfig(agencyId);
		//查询账户余额
		List<Integer> list = validateMoney(orderModels, config);
		DistributeLock lock = null;
		for (OrderModel dbModel : orderModels) {
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + dbModel.getOrderNumber());
				lock.lock();
				//先修改order表
				OrderModel update = new OrderModel();
				update.setId(dbModel.getId());
				update.setStatus(OrderConstant.Status.PROCESSING.key);
				orderDao.updateById(update);
			} finally {
				if (lock != null) {
					lock.unlock();
				}
			}
		}
		return list;
	}

	@Override
	public void transfer(List<Integer> ids, Integer agencyId, Integer actorId) {
		List<OrderModel> orderModels = orderDao.selectBatchIds(ids);
		//获取配置
		PlatformConfig config = getConfig(agencyId);
		try {
			DistributeLock lock = null;
			for (OrderModel orderModel : orderModels) {
				try {
					String orderNumber = orderModel.getOrderNumber();
					String requestId = OrderIdUtil.getPaymentId();
					lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
					lock.lock();
					//查询用户信息
					UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId(), 0);
					// 查询银行卡
					UserBankCardModel bankCardModel = new UserBankCardModel();
					bankCardModel.setUserId(orderModel.getUserId());
					bankCardModel.setStatus(FinanceConstant.BankCardStatus.BIND.key);
					bankCardModel.setType(FinanceConstant.BankCardType.DEFAULT.key);
					bankCardModel = userBankCardDao.selectOne(bankCardModel);
					if (bankCardModel == null) {
						basicPaymentService.dealPaymentFailed(orderModel.getId(), "用户没有默认收款银行卡");
						continue;
					}
					FinanceBankModel bankModel = comFinanceService.getBankModelByBankCodeAndPlatformId(agencyId, bankCardModel.getBankCode());
					//请求畅捷支付
					Map<String, String> map = new HashMap<>();
					try {
						map = repaymentParam(config, orderModel, requestId, bankModel.getThirdBankCode(), bankCardModel, map);
						log.info("操作员：【{}】请求畅捷支付给用户：【{}】打款：【{}】元，请求参数为：{}", actorId == null ? "自动放款" : actorId, userModel.getId(), orderModel.getObtain(), JSON.toJSONString(map));
						String result = ChangJieUtil.buildRequest(map, signType, config.getPrivateKey(), charset, config.getUrl());
						log.info("订单号【{}】请求畅捷打款返回结果为：【{}】", orderNumber, result);
						map.clear();
						JSONObject jsonObject = JSON.parseObject(result);
						map = getCommonMap(jsonObject);
						String resultSign = jsonObject.getString("Sign");
						if (!ChangJieUtil.verifySign(map, resultSign, config.getPublicKey(), charset)) {
							log.error("请求畅捷打款验签失败");
							throw new BusinessException(AdminRespCode.VERIFY_SIGN_ERROR);
						}
						if (map.get("AcceptStatus").equals("S")) {
							if (map.get("PlatformRetCode").equals("0000")) {//成功后更新
								log.info("订单号【{}】请求畅捷打款业务受理成功", orderNumber);
								basicPaymentService.dealPaymentSuccess(orderModel, actorId, userModel, bankCardModel, requestId, FinanceConstant.PayPlatform.CHANGJIE.code);
							}
						} else {
							log.info("订单号【{}】请求畅捷打款业务受理失败：【{}】", orderNumber);
							basicPaymentService.dealPaymentFailed(orderModel.getId(), map.get("PlatformErrorMessage"));
						}
					} catch (Exception e) {
						log.info("请求【畅捷打款】业务受理失败：【{}】", map.get("PlatformErrorMessage"));
						basicPaymentService.dealPaymentFailed(orderModel.getId(), map.get("PlatformErrorMessage"));
					}
				} finally {
					if (lock != null) {
						lock.unlock();
					}
				}
			}
		} finally {
			orderModels.forEach(e -> redisTemplate.delete(UserRedisKey.PAYMENT_CHANGJIE + e.getId()));
		}
	}

	private Map<String, String> repaymentParam(PlatformConfig config, OrderModel orderModel, String request_id, String bankCode, UserBankCardModel bankCardModel, Map<String, String> map) {
		map = setCommonMap(map, config.getSellId());
		map.put("OutTradeNo", request_id);
		map.put("TransCode", "T10100");
		map.put("BusinessType", "0");
		map.put("CorpPushUrl", springProperties.getString("changJie.payNotifyUrl"));
		map.put("BankCommonName", bankCardModel.getBankName());
		map.put("BankCode", bankCode);
		map.put("AcctNo", RSA.encrypt(bankCardModel.getBankCardNum(), config.getPublicKey(), charset));
		map.put("AcctName", RSA.encrypt(bankCardModel.getName(), config.getPublicKey(), charset));
		map.put("TransAmt", FormatUtil.formatDouble2(orderModel.getObtain()));
		map.put("AccountType", "00");
		String str = PaySignUtil.getFinalMap(map);
		String sign = PaySignUtil.generateSign(str, config.getPrivateKey(), charset);
		map.put("Sign", sign);
		map.put("SignType", signType);
		return map;
	}


	private List<Integer> validateMoney(List<OrderModel> orderModels, PlatformConfig config) {
		Double totalMoney = 0.0;
		List<Integer> list = new ArrayList<>();
		for (OrderModel orderModel : orderModels) {
			String redisKey = UserRedisKey.PAYMENT_CHANGJIE + orderModel.getId();
			if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
				throw new BusinessException(AdminRespCode.ORDER_HAS_SUBMIT, "订单：" + orderModel.getOrderNumber() + "已被提交，请勿重复提交");
			}
			totalMoney += orderModel.getObtain();
			list.add(orderModel.getId());
		}
		//开始请求畅捷支付查询余额
		Map<String, String> map = new HashMap<>();
		map.put("TransCode", "C00005");
		map.put("OutTradeNo", OrderIdUtil.getRequestId());
		map = setCommonMap(map, config.getSellId());
		String str = PaySignUtil.getFinalMap(map);
		String sign = PaySignUtil.generateSign(str, config.getPrivateKey(), charset);
		map.put("Sign", sign);
		map.put("SignType", signType);
		String result;
		try {
			result = ChangJieUtil.buildRequest(map, signType, config.getPrivateKey(), charset, config.getUrl());
		} catch (Exception e) {
			log.error("请求畅捷查询余额网络异常！");
			orderModels.forEach(orderModel -> redisTemplate.delete(UserRedisKey.PAYMENT_CHANGJIE + orderModel.getId()));
			throw new BusinessException(AdminRespCode.INTERNET_ERROR,"请求畅捷查询余额网络异常！");
		}
		log.info("请求畅捷查询余额返回结果为：【{}】", result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = getCommonMap(jsonObject);
		String originalRetCode = jsonObject.getString("OriginalRetCode");
		resultMap.put("OriginalRetCode", originalRetCode);
		resultMap.put("RecBalance", jsonObject.getString("RecBalance"));
		String payBalance = jsonObject.getString("PayBalance");
		log.info("畅捷账户余额：【{}】", payBalance);
		resultMap.put("PayBalance", payBalance);
		resultMap.put("OriginalErrorMessage", jsonObject.getString("OriginalErrorMessage"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), charset)) {
			log.error("请求畅捷查询余额验签失败");
			orderModels.forEach(orderModel -> redisTemplate.delete(UserRedisKey.PAYMENT_CHANGJIE + orderModel.getId()));
			throw new BusinessException(AdminRespCode.VERIFY_SIGN_ERROR);
		}
		if (resultMap.get("AcceptStatus").equals("S")) {
			if (resultMap.get("PlatformRetCode").equals("0000") || resultMap.get("PlatformRetCode").equals("2000")) {
				if (resultMap.get("OriginalRetCode").equals("000000")) {
					double remainMoney = Double.valueOf(payBalance) - totalMoney;
					if (remainMoney > 0.0) {
						log.info("畅捷账户余额为：【{}】，不减去手续费交易完成后还剩余【{}】", payBalance, remainMoney);
						return list;
					} else {
						orderModels.forEach(e -> redisTemplate.delete(UserRedisKey.PAYMENT_CHANGJIE + e.getId()));
						log.info("畅捷账户剩余【{}】不足以批付这笔订单", payBalance);
						throw new BusinessException(AdminRespCode.MONEY_NOT_ENOUGH, "账户剩余【" + payBalance + "】不足以批付这笔订单");
					}
				}
			}
		}
		return null;
	}

	/**
	 * 公共请求参数设置
	 */
	public Map<String, String> setCommonMap(Map<String, String> map, String sellId) {
		map.put("Service", "cjt_dsf");
		map.put("Version", "1.0");
		map.put("PartnerId", sellId);//200000400059 生产测试参数
		map.put("InputCharset", charset);// 字符集
		Date date = new Date();
		map.put("TradeDate", SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1));// 商户请求时间
		map.put("TradeTime", new SimpleDateFormat("HHmmss").format(date));// 商户请求时间
		return map;
	}

	/**
	 * 公共返回参数设置
	 */
	public Map<String, String> getCommonMap(JSONObject jsonObject) {
		Map<String, String> map = new HashMap<>();
		map.put("AcceptStatus", jsonObject.getString("AcceptStatus"));
		map.put("InputCharset", jsonObject.getString("InputCharset"));
		map.put("PlatformRetCode", jsonObject.getString("PlatformRetCode"));
		map.put("TimeStamp", jsonObject.getString("TimeStamp"));
		map.put("TransCode", jsonObject.getString("TransCode"));
		map.put("OutTradeNo", jsonObject.getString("OutTradeNo"));
		map.put("FlowNo", jsonObject.getString("FlowNo"));
		map.put("PartnerId", jsonObject.getString("PartnerId"));
		map.put("RetMsg", jsonObject.getString("RetMsg"));
		map.put("Memo", jsonObject.getString("Memo"));
		map.put("PlatformErrorMessage", jsonObject.getString("PlatformErrorMessage"));
		map.put("RetCode", jsonObject.getString("RetCode"));
		map.put("TradeDate", jsonObject.getString("TradeDate"));
		map.put("TradeTime", jsonObject.getString("TradeTime"));
		return map;
	}

	/**
	 * 获取支付配置
	 *
	 * @return
	 */
	@Override
	public PlatformConfig getConfig(Integer agencyId) {
		PlatformConfig config = comFinanceService.getPayPlatformConfig(agencyId);
		return JSON.parseObject(config.getParam(), PlatformConfig.class);
	}

	@Override
	public String getCode() {
		return FinanceConstant.PayPlatform.CHANGJIE.code;
	}
}
