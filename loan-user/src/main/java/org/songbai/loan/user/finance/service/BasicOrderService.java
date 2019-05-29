package org.songbai.loan.user.finance.service;

import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;

public interface BasicOrderService {

    FinanceIOModel initOrder(UserModel user, OrderModel orderModel, String requestId, String bankCardNum, String payPlatform, Integer payType);

    FinanceIOModel initOrder(PayOrderVO orderVO, PayBankCardVO bankCardVO, String requestId, String payPlatform, FinanceConstant.PayType payType);

    void updateIoStatus(FinanceIOModel ioModel, Integer status);

    void repaymentSuccess(FinanceIOModel ioModel);

    void dealOrderFailed(FinanceIOModel ioModel, String errorMsg, Boolean confirm);

    void dealOrderSuccess(FinanceIOModel ioModel);


    void deductSuccess(FinanceIOModel ioModel);

    void deductFailed(FinanceIOModel ioModel, String errorMsg);

    void updateOrderStatus(Integer stage, Integer status, String orderNum);

    /**
     * 还款成功，更新流水
     */
    void repaymentUpdateIo(FinanceIOModel ioModel);
}
