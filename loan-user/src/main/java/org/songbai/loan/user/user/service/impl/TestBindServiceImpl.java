package org.songbai.loan.user.user.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.service.BindService;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wjl
 * @date: 2018/12/19 14:16
 * Description: 测试的通道绑卡
 */
@Service("testBindService")
public class TestBindServiceImpl implements BindService {
	private static final Logger log = LoggerFactory.getLogger(TestBindServiceImpl.class);

	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private UserBankCardService userBankCardService;

	@Override
	public void bind(String phone, UserModel userModel, UserInfoModel infoModel, UserBankCardModel bankCardModel) {
		Integer userId = userModel.getId();
		String oldOrderId = bankCardModel.getRequestId();
		if (StringUtils.isNotBlank(oldOrderId)) {
			bindCodeResend(userId, userModel.getAgencyId(), oldOrderId);
			return;
		}
		UserBankCardModel update = new UserBankCardModel();
		update.setId(bankCardModel.getId());
		update.setRequestId(OrderIdUtil.getRequestId());
		userBankCardDao.updateById(update);
		log.info("----用户：{}在测试通道进行绑卡：{}操作", userId,bankCardModel.getBankCardNum());
		throw new BusinessException(UserRespCode.TEST_ACCOUNT);
	}

	@Override
	public void bindConfirm(String code, UserBankCardModel bankCardModel) {
		String oldOrderId = bankCardModel.getRequestId();
		Integer userId = bankCardModel.getUserId();
		if (StringUtils.isBlank(oldOrderId)) {
			throw new BusinessException(UserRespCode.PLEASE_GET_MSG);
		}
		UserModel userModel = comUserService.selectUserModelById(userId);
		userBankCardService.dealBindSuccess(userModel, bankCardModel);
		log.info("----用户：{}在测试通道进行绑卡：{}成功", userId,bankCardModel.getBankCardNum());
	}

	@Override
	public void bindCodeResend(Integer userId, Integer agencyId, String oldOrderId) {
		throw new BusinessException(UserRespCode.TEST_ACCOUNT);
	}

	@Override
	public void unBind(UserBankCardModel bankCardModel, UserModel userModel) {
		userBankCardService.dealUnBindSuccess(bankCardModel);
	}
}
