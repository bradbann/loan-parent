package org.songbai.loan.user.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.common.finance.RSA;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.BankCardUtil;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.dao.FinanceDao;
import org.songbai.loan.user.finance.service.impl.ChangJiePayServiceImpl;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.helper.ChangJieHelper;
import org.songbai.loan.user.user.service.BindService;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/11 23:15
 * Description:
 */
@Service("changJieBindService")
public class ChangJieBindServiceImpl implements BindService {

	private static final Logger log = LoggerFactory.getLogger(ChangJiePayServiceImpl.class);
	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private SpringProperties springProperties;
	@Autowired
	private ChangJieHelper changJieHelper;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private FinanceDao financeDao;

	private String CHARSET = "UTF-8";

	@Override
	public void bind(String phone, UserModel userModel, UserInfoModel infoModel, UserBankCardModel bankCardModel) {
		Integer userId = userModel.getId();
		PlatformConfig config = changJieHelper.getConfig(userModel.getAgencyId());
		String oldOrderId = bankCardModel.getRequestId();
		if (StringUtils.isNotBlank(oldOrderId)) {
			bindCodeResend(userId, userModel.getAgencyId(), oldOrderId);
			return;
		}
		String requestId = OrderIdUtil.getRequestId();
		Map<String, String> map = bindParam(bankCardModel.getBankCardNum(), phone, userModel, config, infoModel, requestId);

		log.info("user:{} request changJie bind , param:{}", userId, JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("user:{} request changJie bind the returned result：{}", userId, result);

		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		if (resultMap.get("Status").equals("F")) {
			log.info("用户：{}请求畅捷绑卡受理失败，参数为：{}", userId, JSON.toJSONString(map));
			if (resultMap.get("RetMsg").contains("卡号与卡类型不匹配")){
				throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED, "暂不支持信用卡");
			}
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED, resultMap.get("RetMsg"));
		} else {
			UserBankCardModel update = new UserBankCardModel();
			update.setId(bankCardModel.getId());
			update.setRequestId(requestId);
			userBankCardDao.updateById(update);
			log.info("user:{} request changJie bind success", userId);
		}
	}

	@Override
	public void bindConfirm(String code, UserBankCardModel userBankCardModel) {
		//拿配置
		PlatformConfig config = changJieHelper.getConfig(userBankCardModel.getAgencyId());
		String oldOrderId = userBankCardModel.getRequestId();
		Integer userId = userBankCardModel.getUserId();
		if (StringUtils.isBlank(oldOrderId)) {
			throw new BusinessException(UserRespCode.PLEASE_GET_MSG);
		}
		Map<String, String> map = bindConfirmParam(code, config, oldOrderId);
		log.info("user:{} request changJie bind confirm , param:{}", userId, JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("user:{} request changJie bind confirm the returned result：{}", userId, result);

		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		// 此处需要注意 文档写错
		String thirdOrderId = jsonObject.getString("OrderTrxid");
		resultMap.put("OrderTrxid", thirdOrderId);
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		if (resultMap.get("Status").equals("P") || resultMap.get("Status").equals("S")) {
			log.info("user:{} request changJie bind success", userId);
		} else {
			if (resultMap.get("AppRetcode").equals("QT100024") || resultMap.get("AppRetcode").equals("QT100025")) {
				log.info("用户：{}请求畅捷绑卡鉴权失败,原因为：验证码输入错误，参数为：{}", userId, JSON.toJSONString(map));
				throw new BusinessException(UserRespCode.MSG_CODE_ERROR, resultMap.get("AppRetMsg"));
			} else {
				log.info("用户：{}请求畅捷绑卡鉴权失败，参数为：{}", userId, JSON.toJSONString(map));
				throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED, resultMap.get("RetMsg"));
			}
		}
	}

	@Override
	public void bindCodeResend(Integer userId, Integer agencyId, String oldOrderId) {
		//拿配置
		PlatformConfig config = changJieHelper.getConfig(agencyId);
		Map<String, String> map = bindCodeResendParam(config, oldOrderId);
		log.info("request changJie bind codeResend , param:{}", JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("request changJie bind codeResend the returned result：{}", result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		if (!(resultMap.get("Status").equals("P") || resultMap.get("Status").equals("S"))) {
			log.info("用户：{}请求畅捷绑卡短信重发受理失败,参数为：{}", userId, JSON.toJSONString(map));
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED, resultMap.get("RetMsg"));
		}
	}

	@Override
	public void unBind(UserBankCardModel bankCardModel, UserModel userModel) {
		Integer userId = bankCardModel.getUserId();
		List<String> params = financeDao.selectConfigByPlatformCode(userModel.getAgencyId(), FinanceConstant.PayPlatform.CHANGJIE.code, null);
		if (CollectionUtils.isEmpty(params)){
			throw new BusinessException(UserRespCode.REQUEST_UNBIND_FAILED);
		}
		PlatformConfig config = JSON.parseObject(params.get(0),PlatformConfig.class);
		String requestId = OrderIdUtil.getRequestId();
		bankCardModel.setRequestId(requestId);
		Map<String, String> map = unBindParam(bankCardModel, userModel, config);
		log.info("user:{} request changJie unBind , param:{}", userId, JSON.toJSONString(map));
		String result = ChangJieUtil.request(config, map);
		log.info("user:{} request changJie unBind the returned result：{}", userId, result);
		JSONObject jsonObject = JSON.parseObject(result);
		Map<String, String> resultMap = changJieHelper.getCommonMap(jsonObject);
		resultMap.put("TrxId", jsonObject.getString("TrxId"));
		resultMap.put("OrderTrxid", jsonObject.getString("OrderTrxid"));
		resultMap.put("MerUserId", jsonObject.getString("MerUserId"));
		String resultSign = jsonObject.getString("Sign");
		if (!ChangJieUtil.verifySign(resultMap, resultSign, config.getPublicKey(), CHARSET)) {
			throw new BusinessException(UserRespCode.VERIFICATION_FAILED);
		}
		if (resultMap.get("Status").equals("F")) {
			log.info("用户：{}请求畅捷解绑受理失败，参数为：{}", userId, JSON.toJSONString(map));
			throw new BusinessException(UserRespCode.REQUEST_UNBIND_FAILED, resultMap.get("AppRetMsg"));
		}
		userBankCardService.dealUnBindSuccess(bankCardModel);
	}

	private Map<String, String> bindParam(String bankCardNum, String phone, UserModel userModel, PlatformConfig config, UserInfoModel infoModel, String requestId) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_biz_api_auth_req");
		map.put("TrxId", requestId);
		map.put("ExpiredTime", "15m");
		map.put("MerUserId", userModel.getThirdId());
		map.put("NotifyUrl", springProperties.getString("changJie.bindNotifyUrl"));
		map.put("BkAcctTp", "01");
		map.put("IDTp", "01");
		map.put("BkAcctNo", RSA.encrypt(bankCardNum, config.getPublicKey(), CHARSET));
		map.put("IDNo", RSA.encrypt(infoModel.getIdcardNum(), config.getPublicKey(), CHARSET));
		map.put("CstmrNm", RSA.encrypt(infoModel.getName(), config.getPublicKey(), CHARSET));
		map.put("MobNo", RSA.encrypt(phone, config.getPublicKey(), CHARSET));
		map.put("SmsFlag", "1");
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> bindConfirmParam(String code, PlatformConfig config, String oldOrderId) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_api_auth_sms");
		String orderId = OrderIdUtil.getRequestId();
		map.put("TrxId", orderId);
		map.put("OriAuthTrxId", oldOrderId);
		map.put("SmsCode", code);
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> bindCodeResendParam(PlatformConfig config, String oldOrderId) {
		Map<String, String> map = new HashMap<>();
		map.put("TradeType", "auth_order");
		map.put("Service", "nmg_api_quick_payment_resend");
		map.put("TrxId", OrderIdUtil.getRequestId());
		map.put("OriTrxId", oldOrderId);
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}

	private Map<String, String> unBindParam(UserBankCardModel bankCardModel, UserModel userModel, PlatformConfig config) {
		Map<String, String> map = new HashMap<>();
		map.put("Service", "nmg_api_auth_unbind");
		map.put("TrxId", bankCardModel.getRequestId());
		map.put("MerchantNo", config.getSellId());
		map.put("MerUserId", userModel.getThirdId());
		map.put("UnbindType", "0");
		String bankCardNum = bankCardModel.getBankCardNum();
		map.put("CardBegin", BankCardUtil.startSix(bankCardNum));
		map.put("CardEnd", BankCardUtil.endFour(bankCardNum));
		map = changJieHelper.setCommonMap(map, config.getSellId());
		return map;
	}
}
