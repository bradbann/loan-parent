package org.songbai.loan.statistic;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.admin.statistic.ctrl.StatisticCtrl;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.service.StatisticService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceDeductTest {

    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    StatisticCtrl statisticCtrl;
    @Autowired
    StatisticService statisticService;


    @Test
    public void testa() {
        for (int i = 0; i < 1; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("deductId", "2");
            map.put("orderNumber", "L1812061603C9F9W665");

//            map.put("deductId", "3");
//            map.put("orderNumber", "L1812061603C9F9W664");
            jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, JSON.toJSONString(map));
        }

    }


    @Test
    public void statisTest() {
        ReviewStatisPo po = new ReviewStatisPo();
        po.setIsChannelOrder(CommonConst.NO);
        po.setIsProduct(CommonConst.NO);
        po.initLimit();
        System.out.println(statisticService.getAgencyReviewPage(po));
    }
}
