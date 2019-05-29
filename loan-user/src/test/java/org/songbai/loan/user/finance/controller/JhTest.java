package org.songbai.loan.user.finance.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.common.helper.JhPayHelper;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.JhPayModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.user.finance.controller.v2.RepaymentCtrl;
import org.songbai.loan.user.finance.listener.JhPayOrderListener;
import org.songbai.loan.user.finance.service.FinanceIOService;
import org.songbai.loan.user.finance.service.JhPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JhTest {
    @Autowired
    RepaymentCtrl repaymentCtrl;
    @Autowired
    JhPayService jhPayService;
    @Autowired
    JhPayHelper jhPayHelper;
    @Autowired
    JhPayOrderListener jhPayOrderListener;
    @Autowired
    FinanceIOService financeIOService;

    @Test
    public void payTest() {
        Ret ret = jhPayService.pay("L1812131433AVU16GI9", "wxPay", 19);
        System.out.println(ret);
    }

    @Test
    public void scanPayTest() {
        jhPayService.scanPay("L1812131433AVU16GI9", "aliPay", 19);
    }

    @Test
    public void failTest() {

        FinanceIOModel ioModel = financeIOService.getLastIoModelByOrderIdUserId("L1812131433AVU16GI9", 19);
        jhPayService.dealJhOrder(ioModel);
    }

    @Test
    public void autoQuery() {
        jhPayOrderListener.autoQuery();
    }

    @Test
    public void querTest() {
        JhPayModel model = jhPayHelper.queryOrderStatus("H1901311552W814DIUX",18);
        if (model != null) System.out.println(model);
    }

    public static void main(String[] args) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderNumber("L18122717224QWE495");
        orderModel.setPayment(1.11D);
        String requestId = OrderIdUtil.getRepaymentId();
        //生成支付地址
//        String payUrl = jhPayHelper.createPayUrl(orderModel, "", requestId,18);
//        System.out.println(payUrl);
    }

}
