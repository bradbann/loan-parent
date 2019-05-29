package org.songbai.loan.user.user.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.dao.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Author: qmw
 */
@Component
public class CreateUserInfoListener {

	private static final Logger logger = LoggerFactory.getLogger(CreateUserInfoListener.class);
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private AuthenticationDao authenticationDao;
	@Autowired
	private UserBankCardDao bankCardDao;

	/**
	 * 创建用户信息表
	 *
	 * @param jsonObject
	 */
	@JmsListener(destination = JmsDest.CREATE_USER_INFO)
	public void createUserInfo(JSONObject jsonObject) {
		if (logger.isInfoEnabled()) {
			logger.info("begin create user info table,data={}", jsonObject);
		}
		Integer userId = jsonObject.getInteger("userId");
		Integer agencyId = jsonObject.getInteger("agencyId");
		if (userId == null || agencyId == null) {
			logger.info("receive msg user is null");
			return;
		}
		UserInfoModel userInfoModel = new UserInfoModel();
		userInfoModel.setUserId(userId);
		userInfoModel.setAgencyId(agencyId);
		userInfoDao.insert(userInfoModel);

		logger.info("insert loan_u_user_info table success,agencyId={},userId={}", agencyId, userId);

		AuthenticationModel authenticationModel = new AuthenticationModel();
		authenticationModel.setUserId(userId);
		authenticationModel.setAgencyId(agencyId);
		authenticationDao.insert(authenticationModel);

		logger.info("insert loan_u_authentication table success,agencyId={},userId={}", agencyId, userId);

	}
}
