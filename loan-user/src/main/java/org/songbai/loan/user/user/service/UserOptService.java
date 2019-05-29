package org.songbai.loan.user.user.service;

import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.user.user.model.po.UserLoginPO;

/**
 * Author: qmw
 * Date: 2018/10/30 下午12:57
 */
public interface UserOptService {

	UserModel register(UserModel user);

	UserModel login(UserLoginPO loginPO, AppVestModel vest);

	void quickLogin(UserModel dbModel,AppVestModel vest);

	void resetLoginPassByUserId(UserModel dbModel);

	void saveUser(UserModel createUser);

	void enroll(UserModel user);

}
