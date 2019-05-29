package org.songbai.loan.user.finance.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.service.impl.YiBaoPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YibaoPayTest {
    @Autowired
    YiBaoPayServiceImpl yiBaoPayService;
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    ComFinanceService comFinanceService;


    @Test
    public void deductTest() {
        PayOrderVO orderVO = new PayOrderVO();
        orderVO.setAgencyId(18);
        orderVO.setPayment(1.00D);
        PayBankCardVO bankCardVO = new PayBankCardVO();
        bankCardVO.setUserThridId("82b7dc7cdc57413981a53f2b41f140d3");
        bankCardVO.setBankCardNum("62170033200637269161");
        yiBaoPayService.deductPay(orderVO, bankCardVO);
    }

    @Test
    public void jmsTest() {
        String msg = "{\"deductId\":12,\"orderNumber\":\"L18122717224QWE495\"}";
        jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, msg);
    }

    @Test
    public void test(){
        System.out.println("223.93.144.36:3002/user/repayment/yiBaoPayNotify/" + comFinanceService.getAgencyMd5ById(18) + ".do");
    }

}
