package org.songbai.loan.user.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.common.finance.RSA;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.RepaymentService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.helper.ChangJieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/11/22 11:35
 * Description: 畅捷支付service
 */
@Service("changJiePayService")
public class ChangJiePayServiceImpl implements RepaymentService {
	private static final Logger log = LoggerFactory.getLogger(ChangJiePayServiceImpl.class);
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private RedisTemplate<String, Object> redis;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private BasicOrderService basicOrderService;
	@Autowired
	private SpringProperties springProperties;
	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private ChangJieHelper changJieHelper;

	private String CHARSET = "UTF-8";

	@Override
	public void pay(OrderModel orderModel, UserModel userModel, UserInfoModel userInfoModel, UserBankCardModel userBankCardModel) {
		Integer userId = userModel.getId();
		Integer agencyId = userModel.getAgencyId();
		String oldOrderId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
		if (StringUtils.isNotBlank(oldOrderId)) {
			payCodeResend(agencyId, oldOrderId);
			return;
		}
		String bankCardNum = userBankCardModel.getBankCardNum();
		String orderNum = orderModel.getOrderNumber();
		Double money = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());
		String requestId = OrderIdUtil.getRepaymentId();
		PlatformConfig config = changJieHelper.getConfig(agencyId);
		Map<String, String> map = payParam(userBankCardModel, userModel, userInfoModel, money, config, requestId);
		log.info("user:{} request changJie pay , money:{}, param:{}", userId, money, JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("user:{} request changJie pay the returned result：{}", userId, result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		DistributeLock lock = null;
		try {
			lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNum);
			lock.lock();
			if (resultMap.get("Status").equals("F")) {
				log.info("用户：{}请求畅捷支付受理失败,参数为：{}", userId, JSON.toJSONString(map));
				throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, resultMap.get("AppRetMsg"));
			}
			//成功创建订单流水表
			basicOrderService.initOrder(userModel, orderModel, requestId, bankCardNum, FinanceConstant.PayPlatform.CHANGJIE.code,OrderConstant.RepayType.BANKCARD.key);
			redis.opsForHash().put(UserRedisKey.USER_REPAYMENT, userId, requestId);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public void payConfirm(String code, Integer userId) {
		String requestId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
		if (StringUtils.isBlank(requestId)) {
			throw new BusinessException(UserRespCode.REGET_MSG);
		}
		FinanceIOModel ioModel = getAndCheckIoModel(requestId);
		OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
		Double realPayMoney = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());
		if (Double.compare(ioModel.getMoney(), realPayMoney) != 0.0) {//说明订单金额不一样
			log.info("订单金额ioModel:{}与orderModel:{}不一致导致订单失效，请查明原因", ioModel.getMoney(), realPayMoney);
			basicOrderService.dealOrderFailed(ioModel, "订单金额ioModel:" + ioModel.getMoney() + "与orderModel:" + realPayMoney+ "不一致导致订单失效，请查明原因", false);
			throw new BusinessException(UserRespCode.ORDER_HAS_FAILED);
		}
		PlatformConfig config = changJieHelper.getConfig(ioModel.getAgencyId());
		Map<String, String> map = payConfirmParam(code, requestId, config);
		log.info("user:{} request changJie pay confirm, param:{}", userId, JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("user:{} request changJie pay confirm the returned result：{}", userId, result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("PayTrxId", jsonObject.getString("PayTrxId"));
		//文档写错
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		if (!ChangJieUtil.verifySign(resultMap, jsonObject.getString("Sign"), config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		DistributeLock lock = null;
		try {
			lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
			lock.lock();
			if (resultMap.get("Status").equals("P") || resultMap.get("Status").equals("S")) {
				basicOrderService.dealOrderSuccess(ioModel);
			} else {
				if (resultMap.get("AppRetcode").equals("QT100024") || resultMap.get("AppRetcode").equals("QT100025")) {
					log.info("用户：{}请求畅捷支付确认接口受理失败,原因为：验证码输入错误，参数为：{}", userId, JSON.toJSONString(map));
					throw new BusinessException(UserRespCode.MSG_CODE_ERROR, resultMap.get("AppRetMsg"));
				} else {
					log.info("用户：{}请求畅捷支付确认接口受理失败,参数为：{}", userId, JSON.toJSONString(map));
					basicOrderService.dealOrderFailed(ioModel, resultMap.get("AppRetMsg"), false);
					if (resultMap.get("AppRetMsg").contains("余额不足")) {
						redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
						throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, "银行卡余额不足");
					}
					if (resultMap.get("AppRetMsg").contains("订单不存在")) {
						redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
						throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, "订单超时，请重新支付");
					}
					throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, resultMap.get("AppRetMsg"));
				}
			}
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public void payCodeResend(Integer agencyId, String oldOrderId) {
		//拿配置
		PlatformConfig config = changJieHelper.getConfig(agencyId);
		Map<String, String> map = payCodeResendParam(oldOrderId, config);
		//获取结果
		log.info("request changJie pay codeResend , param:{}", JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("request changJie pay codeResend the returned result:{}", result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		if (resultMap.get("Status").equals("P") || resultMap.get("Status").equals("S")) {
			log.info("request changJie pay codeResend success");
		} else {
			log.info("请求畅捷短信重发受理失败");
			FinanceIOModel ioModel = getAndCheckIoModel(oldOrderId);
			basicOrderService.dealOrderFailed(ioModel, resultMap.get("AppRetMsg"), false);
			throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, resultMap.get("AppRetMsg"));
		}
	}

	@Override
	public PayResultVO deductPay(PayOrderVO orderVO, PayBankCardVO bankCardVO) {

        //拿配置
        PlatformConfig config = changJieHelper.getAlreadyReqConfig(orderVO.getAgencyId());
        String requestId = OrderIdUtil.getAutoRepaymentId();

        Map<String, String> map = deductParamWrapper(requestId,config,orderVO,bankCardVO);

        log.info("request changJie deductPay,param:{}", JSON.toJSONString(map));
        String result = ChangJieUtil.request(config, map);
        log.info("request changJie autoPay the returned result：{}", result);

        JSONObject jsonObject = JSON.parseObject(result);

//        Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		// 去掉验签

//        resultMap.put("TrxId", jsonObject.getString("TrxId"));
//        resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));

//        String resultSign = jsonObject.getString("Sign");
//        if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
//            throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
//        }
		DistributeLock lock = null;
		try {
			lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderVO.getOrderNumber());
			lock.lock();
			String status = jsonObject.getString("Status");
			if (status.equals("P") || status.equals("S")) {
				// 受理成功或者是 支付成功的。 需要等待回调。
				FinanceIOModel ioModel = basicOrderService.initOrder(orderVO, bankCardVO, requestId, FinanceConstant.PayPlatform.CHANGJIE.code, FinanceConstant.PayType.DEDUCT);

				basicOrderService.dealOrderSuccess(ioModel);

				//成功创建订单代扣流水表
				return PayResultVO.builder().sts(CommonConst.YES).msg("提交成功")
						.orderNumber(orderVO.getOrderNumber()) .payTrxId(jsonObject.getString("TrxId")).build();
			} else  {
				// 如果是失败的，需要看看订单的状态
				String appRetcode = jsonObject.getString("AppRetcode");

				String msg = jsonObject.getString("AppRetMsg");

				if(StringUtil.isEmpty(msg)){
					msg = jsonObject.getString("RetMsg");
				}

				log.info("请求畅捷【代扣】受理失败[{}],参数为：{}",msg, JSON.toJSONString(map));


				if (Arrays.asList("QT999998","QT999999","QT100001","QT100005" ,"QT100026","QT100027").contains(appRetcode)) {
					// 这几个状态的情况下，可以进行下一次扣款操作。
					return PayResultVO.builder().sts(CommonConst.OK).msg(msg)
							.orderNumber(orderVO.getOrderNumber()).payTrxId(jsonObject.getString("TrxId"))
							.build();
				}else{
					return PayResultVO.builder().sts(CommonConst.NO).msg(msg)
							.orderNumber(orderVO.getOrderNumber()).payTrxId(jsonObject.getString("TrxId"))
							.build();
				}

			}
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public String getCode() {
		return FinanceConstant.PayPlatform.CHANGJIE.code;
	}

	private Map<String, String> payParam(UserBankCardModel bankCardModel, UserModel userModel, UserInfoModel infoModel, Double money, PlatformConfig config, String requestId) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_zft_api_quick_payment");
		map.put("TrxId", requestId);
		map.put("OrdrName", requestId);
		map.put("MerUserId", userModel.getThirdId());
		map.put("SellerId", config.getSellId());
		map.put("ExpiredTime", "15m");
		map.put("BkAcctTp", "01");
		map.put("BkAcctNo", RSA.encrypt(bankCardModel.getBankCardNum(), config.getPublicKey(), CHARSET));
		map.put("IDTp", "01");
		map.put("IDNo", RSA.encrypt(infoModel.getIdcardNum(), config.getPublicKey(), CHARSET));
		map.put("CstmrNm", RSA.encrypt(infoModel.getName(), config.getPublicKey(), CHARSET));
		map.put("MobNo", RSA.encrypt(bankCardModel.getBankPhone(), config.getPublicKey(), CHARSET));
		map.put("TradeType", "11");
		map.put("TrxAmt", FormatUtil.formatDouble2(money));
		map.put("NotifyUrl", springProperties.getString("changJie.rePayNotifyUrl"));
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> payConfirmParam(String code, String requestId, PlatformConfig config) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_api_quick_payment_smsconfirm");
		map.put("TrxId", OrderIdUtil.getRequestId());
		map.put("OriPayTrxId", requestId);
		map.put("SmsCode", code);
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> payCodeResendParam(String oldOrderId, PlatformConfig config) {
		Map<String, String> map = new HashMap<>();
		map.put("TradeType", "pay_order");
		map.put("Service", "nmg_api_quick_payment_resend");
		map.put("TrxId", OrderIdUtil.getRequestId());
		map.put("OriTrxId", oldOrderId);
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

//	private Map<String, String> autoPayParam(OrderModel orderModel, UserInfoModel infoModel, UserBankCardModel bankCardModel, String requestId, PlatformConfig config) {
//		Map<String, String> map = new HashMap<>();
//		map.put("Service", "nmg_api_quickpay_withhold");
//		map.put("TrxId", requestId);
//		map.put("OrdrName", "业务扣款");
//		map.put("SellerId", config.getSellId());
//		map.put("ExpiredTime", "5m");
//		map.put("BkAcctNo", RSA.encrypt(bankCardModel.getBankCardNum(), config.getPublicKey(), CHARSET));
//		map.put("IDTp", "01");
//		map.put("IDNo", RSA.encrypt(infoModel.getIdcardNum(), config.getPublicKey(), CHARSET));
//		map.put("CstmrNm", RSA.encrypt(infoModel.getName(), config.getPublicKey(), CHARSET));
//		map.put("MobNo", RSA.encrypt(bankCardModel.getBankPhone(), config.getPublicKey(), CHARSET));
//		map.put("TradeType", "11");
//		//TODO 按比例去扣款
//		map.put("TrxAmt", FormatUtil.formatDouble2(orderModel.getPayment()));
//		map = changJieHelper.setCommonMap(map, config.getSellId());
//		return map;
//	}


	private Map<String,String> deductParamWrapper(String requestId,PlatformConfig config,  PayOrderVO orderVO, PayBankCardVO bankCardVO){
		Map<String, String> map = new HashMap<>();

		map.put("Service", "nmg_biz_api_quick_payment");
		map.put("TrxId", requestId);
		map.put("OrdrName", "repay");
		map.put("MerUserId", bankCardVO.getUserThridId());
		map.put("SellerId", config.getSellId());
		map.put("ExpiredTime", "3m");
		map.put("TradeType", "11");
		map.put("CardBegin", bankCardVO.getBankCardNum().substring(0,6)); // 卡号前6位
		map.put("CardEnd", bankCardVO.getBankCardNum().substring(bankCardVO.getBankCardNum().length() - 4 )); //卡号后4位
		map.put("TrxAmt", FormatUtil.formatDouble2(orderVO.getPayment()));
		map.put("NotifyUrl", springProperties.getString("changJie.rePayNotifyUrl"));
		// "http://223.93.144.36:8060/user/repayment/changJiePayNotify.do");
		map.put("SmsFlag", "0");


		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> queryParam(FinanceIOModel ioModel, PlatformConfig config) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_api_query_trade");
		map.put("TrxId", OrderIdUtil.getRequestId());
		map.put("OrderTrxId", ioModel.getRequestId());
		map.put("TradeType", "pay_order");
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private FinanceIOModel getAndCheckIoModel(String requestId) {
		FinanceIOModel ioModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
		if (ioModel == null) {
			throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
		}
		return ioModel;
	}
}