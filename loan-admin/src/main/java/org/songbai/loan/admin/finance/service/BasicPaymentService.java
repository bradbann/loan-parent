package org.songbai.loan.admin.finance.service;

import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;

public interface BasicPaymentService {

	/**
	 * 打款成功同步响应逻辑，只有打款成功才会插入opt和io表
	 */
	void dealPaymentSuccess(OrderModel orderModel, Integer actorId, UserModel userModel, UserBankCardModel bankCardModel, String requestId, String payPlatform);

	/**
	 * 打款失败同步响应逻辑, 此处只需更新order表就可以啦
	 */
	void dealPaymentFailed(Integer orderId, String msg);

	/**
	 * 打款失败异步响应逻辑  （最终状态->打款成功） 更新order表和io表
	 */
	void paymentFailed(OrderModel orderModel, FinanceIOModel ioModel, String msg);

	/**
	 * 打款成功异步响应逻辑  （最终状态->打款成功） 更新order表和io表和插入流水表
	 */
	void paymentSuccess(OrderModel orderModel, FinanceIOModel ioModel, String name);

}
