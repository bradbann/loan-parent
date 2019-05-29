package org.songbai.loan.user.finance.service;

import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;

public interface OrderPayService {

    /**
     *
     * @param ioModel
     */
    void payOrderForDeduct(FinanceIOModel ioModel);


    /**
     * 检查用户的应还金额是不是换完了， 还完了，那么就结束了。
     * @return  true:全部还完了， false:部分还款
     */
    boolean updateOrderStatusForRepayment(OrderModel orderModel);


    void payOrderForDeductFail(FinanceIOModel ioModel);

}
