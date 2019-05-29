package org.songbai.loan.user.finance.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.PayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayNotifyServiceImpl implements PayNotifyService {

    @Autowired
    private BasicOrderService basicOrderService;


    @Override
    public void paySuccess(FinanceIOModel ioModel) {

        String requestId = ioModel.getRequestId();

        String type = requestId.substring(0, 1);


        switch (type) {
            case OrderIdUtil.DEDUCT_ORDER:
                // 自动扣款
                log.info("畅捷支付回调的用户的[{}],订单号：{} 还款成功", ioModel.getUserId(), ioModel.getOrderId());
                basicOrderService.deductSuccess(ioModel);
                break;
            default:

                log.info("畅捷支付回调的用户的[{}],订单号：{} 还款成功", ioModel.getUserId(), ioModel.getOrderId());
                basicOrderService.repaymentSuccess(ioModel);

                break;

        }


    }

    @Override
    public void payFail(FinanceIOModel ioModel, String msg) {
        String requestId = ioModel.getRequestId();


        String type = requestId.substring(0, 1);


        switch (type) {
            case OrderIdUtil.DEDUCT_ORDER:
                // 自动扣款
                log.info("畅捷支付回调的用户的[{}], 订单号{}, 还款失败", ioModel.getUserId(), ioModel.getOrderId());
                basicOrderService.deductFailed(ioModel, msg);
                break;
            default:
                log.info("畅捷支付回调的用户的[{}], 订单号{}, 还款失败", ioModel.getUserId(), ioModel.getOrderId());
                basicOrderService.dealOrderFailed(ioModel, msg, true);
                break;

        }


    }


}
