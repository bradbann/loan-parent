package org.songbai.loan.statistic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.model.statistic.dto.PayStatisticDTO;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.statistic.listener.FinanceListener;
import org.songbai.loan.statistic.listener.UserOptListener;
import org.songbai.loan.statistic.push.OrderPushListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceProcessorTest {
    @Autowired
    FinanceListener processor;
    @Autowired
    private UserOptListener userOptListener;
    @Autowired
    private OrderPushListener pushListener;
    @Test
    public void test() {
        for (int i = 0; i < 3; i++) {
            PayStatisticDTO dto = new PayStatisticDTO();
            dto.setAgencyId(0);
            dto.setLoan(1000D);
            dto.setPay(750D);
            dto.setStampTax(250D);
            dto.setPayDate(LocalDate.now());
            dto.setRepayDate(LocalDate.now().plusDays(7));
            dto.setVestId(7);
            if (i < 2) {
                dto.setIsFirstLoan(1);
            } else {
                dto.setIsAgainLoan(1);
            }
            processor.payProcessor(dto);
        }
        repay();
    }

    @Test
    public void u() {
        UserStatisticDTO d = new UserStatisticDTO();
        d.setRegisterDate(LocalDate.now().plusDays(2));
        d.setAgencyId(13);
        d.setChannelCode("default");
        d.setActionDate(LocalDate.now().plusDays(2));
        d.setVestId(16);

        d.setIsLogin(1);
        d.setIsAli(1);
        d.setIsNew(1);
        userOptListener.userStatistic(d);
    }

    @Test
    public void repay() {
        //for (int i = 0; i < 5; i++) {
        //
        //pushListener.sendMsgUserRepayOrdertTomorrow();
        //}
        //pushListener.pushOrderRepayRemind();
        ////for (int i = 0; i < 3; i++) {
        ////    PayStatisticDTO dto = new PayStatisticDTO();
        ////    dto.setAgencyId(0);
        ////    dto.setLoan(1000D);
        ////    dto.setPay(750D);
        ////    dto.setStampTax(250D);
        ////    dto.setPayDate(LocalDate.now());
        ////    dto.setRepayDate(LocalDate.now().plusDays(7));
        ////    if (i < 2) {
        ////        dto.setIsFirstLoan(1);
        ////    } else {
        ////        dto.setIsAgainLoan(1);
        ////    }
        ////    processor.payProcessor(dto);
        ////}
        // 提前还款
        RepayStatisticDTO dto = new RepayStatisticDTO();
        dto.setAgencyId(0);
        dto.setRepayDate(LocalDate.now().plusDays(7));
        dto.setIsEarly(1);
        dto.setVestId(7);

        dto.setRepayMoney(1000D);

        processor.repayProcessor(dto);
    }
}
