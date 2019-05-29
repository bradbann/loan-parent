package org.songbai.loan.user.finance.service;

import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;

public interface RepaymentService {

	void pay(OrderModel orderModel, UserModel userModel, UserInfoModel userInfoModel, UserBankCardModel userBankCardModel);

	void payConfirm(String code, Integer userId);

	void payCodeResend(Integer agencyId, String oldOrderId);

//	void autoPay(OrderModel orderModel, UserModel userModel, UserInfoModel infoModel, UserBankCardModel bankCardModel, Integer actorId);
//
//	void payQuery(FinanceIOModel ioModel);

	String getCode();

	PayResultVO deductPay(PayOrderVO orderVO, PayBankCardVO bankCardVO);

}
