package org.songbai.loan.user.finance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.loan.common.helper.LimitRequestHelper;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.finance.service.FinanceIOService;
import org.songbai.loan.user.finance.service.RepaymentService;
import org.songbai.loan.user.finance.service.impl.RepaymentFactory;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wjl
 * @date: 2018/11/22 14:34
 * Description: 前台还款Controller
 */
@RestController
@RequestMapping("/repayment")
public class RepaymentController {
	private static final Logger log = LoggerFactory.getLogger(RepaymentController.class);

	@Autowired
	private LimitRequestHelper limitRequestHelper;
	@Autowired
	private UserBankCardDao bankCardDao;
	@Autowired
	private RepaymentFactory repaymentFactory;
	@Autowired
	private ComFinanceService comFinanceService;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private FinanceIOService ioService;
	@Autowired
	private RedisTemplate<String, Object> redis;

	@PostMapping("/v1/pay")
	public Response pay(String orderNum, String bankCardNum) {
		Assert.hasText(orderNum, "订单号不能为空");
		Assert.hasText(bankCardNum, "银行卡号不能为空");
		Integer userId = UserUtil.getUserId();
		limitRequestHelper.payMsgLimit(orderNum, userId);
		validateAndChooseMethod(orderNum, bankCardNum, userId, FinanceConstant.PAY);
		return Response.success();
	}

	@PostMapping("/v1/payConfirm")
	public Response payConfirm(String code) {
		Assert.hasText(code, "验证码不能为空");
		if (code.length() != 6) throw new BusinessException(UserRespCode.MSG_CODE_ERROR);
		Integer userId = UserUtil.getUserId();
		validateAndChooseMethod(code, null, userId, FinanceConstant.PAYCONFIRM);
		return Response.success();
	}

	@PostMapping("/checkOrder")
	public Response checkOrder(String orderNum, String bankCardNum) {
		Assert.hasText(orderNum, "订单号不能为空");
		Assert.hasText(bankCardNum, "银行卡号不能为空");
		Integer userId = UserUtil.getUserId();
		checkAndGetOrderModel(orderNum, bankCardNum, userId);
		return Response.success();
	}

	//如果是支付 param 意味 orderNum ， 如果是支付确认 param 意味 code
	public void validateAndChooseMethod(String param, String bankCardNum, Integer userId, String type) {
		//先查询用户这个卡号是不是解绑了
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null) {
			throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS);
		}
		Integer agencyId = userModel.getAgencyId();
		UserInfoModel userInfoModel = comUserService.findUserInfoByUserId(userId);
		if (userInfoModel == null) {
			throw new BusinessException(UserRespCode.USER_ACCOUNT_ERROR);
		}
		UserBankCardModel bankCardModel = null;
		if (type.equals(FinanceConstant.PAY)) {
			bankCardModel = bankCardDao.getBankCardByCardNum(bankCardNum, FinanceConstant.BankCardStatus.BIND.key, agencyId);
			if (bankCardModel == null) {
				throw new BusinessException(UserRespCode.PLEASE_AUTH);
			}
		}
		String platformCode = comFinanceService.getPayCodeByAgency(agencyId);
		RepaymentService bean = repaymentFactory.getBeanByCode(platformCode);
		if (bean == null){
			throw new BusinessException(UserRespCode.DO_NOT_DELETE_TABLE);
		}
		if (type.equals(FinanceConstant.PAY)) {
			OrderModel orderModel = ioService.validateOrder(param, userId);
			bean.pay(orderModel, userModel, userInfoModel, bankCardModel);
		} else {
			bean.payConfirm(param, userId);
		}
	}

	private void checkAndGetOrderModel(String param, String bankCardNum, Integer userId) {
		ioService.validateOrder(param, userId);
		//根据userId和订单号查询最新的一条  判断bankNum 是不是 与这次的一致 不一致则删除io这个记录 以及opt表
		FinanceIOModel ioModel = ioService.getLastIoModelByOrderIdUserId(param, userId);
		if (ioModel != null && !ioModel.getBankCardNum().equals(bankCardNum)) {//说明上涨卡的订单未成功，只走到了请求接口
			//查询opt表是否也是进行中状态
			OrderOptModel optModel = ioService.getLastOptModelByOrderIdUserId(param, userId);
			if (optModel != null) {
				log.info("用户{}使用银行卡{}请求但未支付，现在使用银行卡{}，删除原有的io表和opt表记录", userId, ioModel.getBankCardNum(), bankCardNum);
				ioService.deleteIoModelOptModel(ioModel.getId(), optModel.getId());
				redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
			} else {//这就说明io表正常 opt表不正常，可能删除表了，所以联系下客服处理
				redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
				throw new BusinessException(UserRespCode.DO_NOT_DELETE_TABLE, "数据异常，请联系客服处理");
			}
		}
	}

	@Deprecated
	@PostMapping("/pay")
	public Response pay(String orderNum) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

	@Deprecated
	@PostMapping("/payConfirm")
	public Response payConfirmOld(String code) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}
}
