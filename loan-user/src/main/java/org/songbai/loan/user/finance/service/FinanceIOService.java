package org.songbai.loan.user.finance.service;

import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;

public interface FinanceIOService {

	void deleteIoModelOptModel(Integer ioId,Integer optId);

	FinanceIOModel getLastIoModelByOrderIdUserId(String orderId, Integer userId);

	OrderOptModel getLastOptModelByOrderIdUserId(String orderId, Integer userId);

	//还款校验order
	OrderModel validateOrder(String orderNum, Integer userId);

    FinanceIOModel getIoModelByOrderIdAndRequestId(String orderNum, String requestId, Integer userId);
}
