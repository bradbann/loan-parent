package org.songbai.loan.statistic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.admin.news.dao.UserFeedbackDao;
import org.songbai.loan.constant.risk.RiskJmsDest;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.songbai.loan.constant.JmsDest.SCHEDULE_ORDER_WAITDATA;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsTemplateTest {

    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    private UserFeedbackDao userFeedbackDao;

    @Test
    public void test() {
        UserFeedbackModel userFeedbackModel = new UserFeedbackModel();
        userFeedbackModel.setContent("😆😆😆");

//        testExtractVar();

        jmsTemplate.convertAndSend(SCHEDULE_ORDER_WAITDATA, "{}");
    }


    @Test
    public void testExtractVar() {
        VariableExtractVO vo = new VariableExtractVO();
        vo.setUserId("db145236381d4e66a1052f04304cdaab");


        vo.setSources(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT);
        vo.setTaskId("44cdc640-ee54-11e8-a585-00163e0e0050");
        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);


        vo.setSources(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT);
        vo.setTaskId("ba5e0eba-ee54-11e8-a1ca-00163e12d150");
        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);


        vo.setSources(VariableConst.VAR_SOURCE_MOXIE_REPORT);
        vo.setTaskId("ba5e0eba-ee54-11e8-a1ca-00163e12d150");
        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);


        vo.setOrderNumber("L1811231340R5ULSI25");
        vo.setSources(VariableConst.VAR_SOURCE_PLATFORM_BASE);
        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);


        vo.setOrderNumber("L1811231340R5ULSI25");
        vo.setSources(VariableConst.VAR_SOURCE_PLATFORM_CONTACTS);
        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);


    }

}
