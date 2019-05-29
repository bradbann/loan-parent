package org.songbai.loan.admin.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.finance.service.PaymentService;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.resp.UserRespCode;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/11/30 13:44
 * Description:
 */
@Service("yiBaoPaymentService")
public class YiBaoPaymentServiceImpl implements PaymentService {
	private static final Logger log = LoggerFactory.getLogger(YiBaoPaymentServiceImpl.class);

	@Autowired
	private OrderDao orderDao;
	@Autowired
	private RedisTemplate<String, Integer> redisTemplate;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private ComFinanceService comFinanceService;
	@Autowired
	private BasicPaymentService basicPaymentService;
	@Autowired
	private ComUserService comUserService;

	@Override
	public List<Integer> validate(List<OrderModel> orderModels, Integer agencyId, Integer actorId) {
		//查询账户余额
		String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
		List<Integer> list = validateMoney(orderModels, appKey);
		DistributeLock lock = null;
		for (OrderModel orderModel : orderModels) {
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
				lock.lock();
				//先修改order表
				OrderModel update = new OrderModel();
				update.setId(orderModel.getId());
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
		try {
			DistributeLock lock = null;
			for (OrderModel orderModel : orderModels) {
				try {
					String orderNumber = orderModel.getOrderNumber();
					String requestId = SimpleDateFormatUtil.dateToString(new Date(), SimpleDateFormatUtil.DATE_FORMAT9) + orderModel.getId();
					lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
					lock.lock();
					//查询用户信息
					UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId(), 0);
					UserBankCardModel bankCardModel = comFinanceService.getUserDefaultBankCard(orderModel.getUserId());
					if (bankCardModel == null) {
						basicPaymentService.dealPaymentFailed(orderModel.getId(), "用户没有默认收款银行卡");
						continue;
					}
					FinanceBankModel bankModel = comFinanceService.getBankModelByBankCodeAndPlatformId(agencyId, bankCardModel.getBankCode());
					//请求易宝支付
					String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
					YopRequest yopRequest = requestParam(orderModel, requestId, bankCardModel, bankModel, appKey);
					log.info("操作员：【{}】请求易宝支付给用户：【{}】打款：【{}】元，请求参数为：{}", actorId, userModel.getId(), orderModel.getObtain(), JSON.toJSONString(yopRequest));
					YopResponse yopResponse;
					try {
						yopResponse = YopClient3.postRsa("/rest/v1.0/balance/transfer_send", yopRequest);
						if (StringUtils.isBlank(yopResponse.getStringResult())) {
							log.info("请求易宝支付返回：{}", JSON.toJSONString(yopResponse));
							throw new BusinessException("请求易宝失败");
						}
					} catch (Exception e) {
						log.error("请求易宝转账时网络错误", e);
						throw new BusinessException(UserRespCode.INTERNET_ERROR, "请求易宝转账时网络错误");
					}
					Map<String, String> result = YiBaoUtil.parseResponse(yopResponse.getStringResult());
					log.info("订单号【{}】请求易宝打款返回结果为：【{}】", orderNumber, JSON.toJSONString(result));
					if (result.get("errorCode").equals("BAC001")) {
						if (result.get("transferStatusCode").equals("0025") || result.get("transferStatusCode").equals("0026")) {
							log.info("订单号【{}】请求易宝打款成功", orderNumber);
							basicPaymentService.dealPaymentSuccess(orderModel, actorId, userModel, bankCardModel, requestId, FinanceConstant.PayPlatform.YIBAO.code);
						} else {
							log.info("订单号【{}】请求易宝打款失败", orderNumber);
							basicPaymentService.dealPaymentFailed(orderModel.getId(), result.get("PlatformErrorMessage"));
						}
					} else {
						log.info("订单号【{}】请求易宝打款失败", orderNumber);
						basicPaymentService.dealPaymentFailed(orderModel.getId(), result.get("PlatformErrorMessage"));
					}
				} finally {
					if (lock != null) {
						lock.unlock();
					}
				}
			}
		} finally {
			orderModels.forEach(orderModel -> redisTemplate.delete(UserRedisKey.PAYMENT_YIBAO + orderModel.getId()));
		}
	}

	private YopRequest requestParam(OrderModel orderModel, String requestId, UserBankCardModel bankCardModel, FinanceBankModel bankModel, String appKey) {
		appKey = appKey.replace("SQKK", "OPR:");
		YopRequest yopRequest = new YopRequest(appKey);
		String merchantNo = appKey.substring(4);
		yopRequest.addParam("customerNumber", merchantNo);
		yopRequest.addParam("groupNumber", merchantNo);
		yopRequest.addParam("batchNo", requestId);
		yopRequest.addParam("orderId", requestId);
		yopRequest.addParam("amount", FormatUtil.formatDouble2(orderModel.getObtain()));
		yopRequest.addParam("urgency", "1");
		yopRequest.addParam("accountName", bankCardModel.getName());
		yopRequest.addParam("accountNumber", bankCardModel.getBankCardNum());
		yopRequest.addParam("bankCode", bankModel.getThirdBankCode());
		yopRequest.addParam("feeType", "SOURCE");
		return yopRequest;
	}

	private List<Integer> validateMoney(List<OrderModel> orderModels, String appKey) {
		Double totalMoney = 0.0;
		List<Integer> list = new ArrayList<>();
		for (OrderModel orderModel : orderModels) {
			String redisKey = UserRedisKey.PAYMENT_YIBAO + orderModel.getId();
			if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
				throw new BusinessException(AdminRespCode.ORDER_HAS_SUBMIT, "订单：" + orderModel.getOrderNumber() + "已被提交，请勿重复提交");
			}
			totalMoney += orderModel.getObtain();
			list.add(orderModel.getId());
		}
		//查询易宝的余额
		appKey = appKey.replace("SQKK", "OPR:");
		YopRequest yopRequest = new YopRequest(appKey);
		yopRequest.addParam("customerNumber", appKey.substring(4));
		log.info("请求查询易宝余额的参数为：{}", JSON.toJSONString(yopRequest));
		YopResponse yopResponse;
		try {
			yopResponse = YopClient3.postRsa("/rest/v1.0/balance/query_customer_amount", yopRequest);
			if (StringUtils.isBlank(yopResponse.getStringResult())) {
				log.info("查询易宝余额返回：{}", JSON.toJSONString(yopResponse));
				throw new BusinessException("请求易宝失败");
			}
		} catch (Exception e) {
			log.error("请求易宝查询余额时网络错误", e);
			orderModels.forEach(orderModel -> redisTemplate.delete(UserRedisKey.PAYMENT_YIBAO + orderModel.getId()));
			throw new BusinessException(UserRespCode.INTERNET_ERROR, "请求易宝查询余额失败");
		}
		Map<String, String> result = YiBaoUtil.parseResponse(yopResponse.getStringResult());
		Double payBalance = Double.valueOf(result.get("wtjsValidAmount"));//总余额
		log.info("易宝账户余额：【{}】", payBalance);
		if (payBalance > 0.0) {
			double remainMoney = payBalance - totalMoney;
			if (remainMoney > 0.0) {
				log.info("易宝账户余额为：【{}】，不减去手续费交易完成后还剩余【{}】", payBalance, remainMoney);
				return list;
			} else {
				orderModels.forEach(e -> redisTemplate.delete(UserRedisKey.PAYMENT_YIBAO + e.getId()));
				log.info("易宝账户剩余【{}】不足以批付这笔订单", payBalance);
				throw new BusinessException(AdminRespCode.MONEY_NOT_ENOUGH, "易宝账户剩余【" + payBalance + "】不足以批付这笔订单");
			}
		} else {
			orderModels.forEach(e -> redisTemplate.delete(UserRedisKey.PAYMENT_YIBAO + e.getId()));
			log.info("易宝账户剩余【{}】不足以批付这笔订单", payBalance);
			throw new BusinessException(AdminRespCode.MONEY_NOT_ENOUGH, "易宝账户不足剩余0.0元");
		}
	}

	/**
	 * 获取支付配置
	 *
	 * @return
	 */
	@Override
	public PlatformConfig getConfig(Integer agencyId) {
		return null;
	}

	@Override
	public String getCode() {
		return FinanceConstant.PayPlatform.YIBAO.code;
	}

}
