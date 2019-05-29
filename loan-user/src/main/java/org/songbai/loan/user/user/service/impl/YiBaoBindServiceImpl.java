package org.songbai.loan.user.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.BankCardUtil;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.dao.FinanceDao;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.service.BindService;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/13 17:18
 * Description: 易宝绑卡相关
 */
@Service("yiBaoBindService")
public class YiBaoBindServiceImpl implements BindService {
	private static final Logger log = LoggerFactory.getLogger(YiBaoBindServiceImpl.class);
	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private FinanceDao financeDao;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ComFinanceService comFinanceService;

	@Override
	public void bind(String phone, UserModel userModel, UserInfoModel infoModel, UserBankCardModel bankCardModel) {
		Integer userId = userModel.getId();
		String oldOrderId = bankCardModel.getRequestId();
		if (StringUtils.isNotBlank(oldOrderId)) {
			bindCodeResend(userId, userModel.getAgencyId(), oldOrderId);
			return;
		}
		String requestId = OrderIdUtil.getRequestId();
		String appKey = comFinanceService.getYiBaoSellIdByAgencyId(userModel.getAgencyId());
		YopRequest yopRequest = bindParam(bankCardModel.getBankCardNum(), phone, userModel, infoModel, requestId, appKey);
		log.info("user:{} request yiBao bind , param:{}", userId, JSON.toJSONString(yopRequest));
		Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/unified/auth/request", yopRequest);
		log.info("user:{} request yiBao bind the returned result：{}", userId, JSON.toJSONString(result));
		// 失败则进行相关处理
		if (!result.get("status").equals("TO_VALIDATE")) {
			log.info("user:{} request yiBao bind failed", userId);
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED);

		}
		UserBankCardModel update = new UserBankCardModel();
		update.setId(bankCardModel.getId());
		update.setRequestId(requestId);
		userBankCardDao.updateById(update);
		log.info("user:{} request yiBao bind success", userId);

	}

	@Override
	public void bindConfirm(String code, UserBankCardModel userBankCardModel) {
		String oldOrderId = userBankCardModel.getRequestId();
		Integer userId = userBankCardModel.getUserId();
		if (StringUtils.isBlank(oldOrderId)) {
			throw new BusinessException(UserRespCode.PLEASE_GET_MSG);
		}
		String appKey = comFinanceService.getYiBaoSellIdByAgencyId(userBankCardModel.getAgencyId());
		YopRequest yopRequest = new YopRequest(appKey);
		yopRequest.addParam("merchantno", appKey);
		yopRequest.addParam("requestno", oldOrderId);
		yopRequest.addParam("validatecode", code);
		log.info("user:{} request yiBao bind confirm , param:{}", userId, JSON.toJSONString(yopRequest));
		Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/auth/confirm", yopRequest);
		log.info("user:{} request yiBao bind confirm the returned result：{}", userId, JSON.toJSONString(result));
		// 失败则进行相关处理
		if (!result.get("status").equals("BIND_SUCCESS")) {
			log.info("user:{} request yiBao bind failed", userId);
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED, result.get("errormsg"));
		}
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null) {
			throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS);
		}
		userBankCardService.dealBindSuccess(userModel, userBankCardModel);

	}

	@Override
	public void bindCodeResend(Integer userId, Integer agencyId, String oldOrderId) {
		String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
		YopRequest yopRequest = new YopRequest(appKey);
		yopRequest.addParam("merchantno", appKey);
		yopRequest.addParam("requestno", oldOrderId);
		yopRequest.addParam("advicesmstype", "MESSAGE");
		log.info("user:{} request yiBao bind codeResend , param:{}", userId, JSON.toJSONString(yopRequest));
		Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/auth/resend", yopRequest);
		log.info("user:{} request yiBao bind codeResend the returned result：{}", userId, JSON.toJSONString(result));
		// 失败则进行相关处理
		if (!result.get("status").equals("TO_VALIDATE")) {
			log.info("user:{} request yiBao bind codeResend failed", userId);
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED);
		}

	}

	@Override
	public void unBind(UserBankCardModel bankCardModel, UserModel userModel) {
		Integer userId = bankCardModel.getUserId();
		List<String> params = financeDao.selectConfigByPlatformCode(userModel.getAgencyId(), FinanceConstant.PayPlatform.YIBAO.code, null);
		if (CollectionUtils.isEmpty(params)) {
			throw new BusinessException(UserRespCode.REQUEST_UNBIND_FAILED);
		}
		PlatformConfig config = JSON.parseObject(params.get(0), PlatformConfig.class);
		String requestId = OrderIdUtil.getRequestId();
		bankCardModel.setRequestId(requestId);
		YopRequest yopRequest = unBindParam(bankCardModel, userModel, config.getSellId());
		Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/unbind/request", yopRequest);
		log.info("user:{} request yiBao unBind the returned result：{}", userId, JSON.toJSONString(result));
		// 失败则进行相关处理
		if (result.get("status").equals("FAIL")) {
			log.info("user:{} request yiBao unBind failed", userId);
			throw new BusinessException(UserRespCode.REQUEST_UNBIND_FAILED);
		}
		userBankCardService.dealUnBindSuccess(bankCardModel);

	}

	private YopRequest unBindParam(UserBankCardModel bankCardModel, UserModel userModel, String appKey) {
		YopRequest yopRequest = new YopRequest(appKey);
		yopRequest.addParam("merchantno", appKey);
		yopRequest.addParam("requestno", bankCardModel.getRequestId());
		yopRequest.addParam("identityid", userModel.getThirdId());
		yopRequest.addParam("identitytype", "USER_ID");
		yopRequest.addParam("cardtop", BankCardUtil.startSix(bankCardModel.getBankCardNum()));
		yopRequest.addParam("cardlast", BankCardUtil.endFour(bankCardModel.getBankCardNum()));
		return yopRequest;
	}

	private YopRequest bindParam(String bankCardNum, String phone, UserModel userModel, UserInfoModel infoModel, String requestId, String appKey) {
		YopRequest yopRequest = new YopRequest(appKey);
		yopRequest.addParam("merchantno", appKey);
		yopRequest.addParam("requestno", requestId);
		yopRequest.addParam("identityid", userModel.getThirdId());
		yopRequest.addParam("identitytype", "USER_ID");
		yopRequest.addParam("cardno", bankCardNum);
		yopRequest.addParam("idcardno", infoModel.getIdcardNum());
		yopRequest.addParam("idcardtype", "ID");
		yopRequest.addParam("username", infoModel.getName());
		yopRequest.addParam("phone", phone);
		yopRequest.addParam("issms", "true");
		yopRequest.addParam("avaliabletime", "15");
		yopRequest.addParam("requesttime", SimpleDateFormatUtil.dateToString(new Date(), SimpleDateFormatUtil.DATE_FORMAT6));
		//TODO （注：商户在易宝需开通拥有鉴权验四的鉴权类型）
		yopRequest.addParam("authtype", "COMMON_FOUR");
		return yopRequest;
	}

}
