package org.songbai.loan.user.user.service;

import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;

public interface BindService {

	void bind(String phone, UserModel userModel, UserInfoModel infoModel, UserBankCardModel bankCardModel);

	void bindConfirm(String code, UserBankCardModel bankCardModel);

	void bindCodeResend(Integer userId, Integer agencyId, String oldOrderId);

	void unBind(UserBankCardModel bankCardModel, UserModel userModel);
}
