package org.songbai.loan.user.user.service;

import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;

import java.util.Map;

public interface UserBankCardService {

	void save(String name, String bankName, String bankCardNum, String bankCode, String bankPhone, Integer userId);

	void bind(String phone, UserBankCardModel bankCardModel, Integer userId);

	void bindConfirm(String code, UserBankCardModel bankCardModel);

	void unBind(Integer userId, Integer id);

	void bindDefault(Integer userId, Integer id);

	String getBankByBankNum(String bankNum);

	/**
	 * 绑卡成功之后的逻辑处理
	 */
	void dealBindSuccess(UserModel userModel, UserBankCardModel userBankCardModel);

	/**
	 * 解绑成功之后的逻辑处理
	 */
	void dealUnBindSuccess(UserBankCardModel bankCardModel);

	Map<String, Object> list(Integer userId);

	Map<String, Object> all(Integer userId);

}
