package org.songbai.loan.risk.platform.helper;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.constant.risk.RiskJmsDest;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RiskNotifyJmsHelper {


    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 通知抽取变量
     */
    public void notifyVariableExtract(String sources, String userId, String taskId) {


        log.info("用户{},{}数据{}已经准备完成，并且通知抽取变量", userId, taskId, sources);


        VariableExtractVO vo = new VariableExtractVO();

        vo.setSources(sources);
        vo.setUserId(userId);
        vo.setTaskId(taskId);


        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);
    }

    /**
     * 通知抽取变量
     */
    public void notifyVariableExtract(String sources, String userId) {


        log.info("用户{},{}数据{}已经准备完成，并且通知抽取变量", userId, sources);


        VariableExtractVO vo = new VariableExtractVO();

        vo.setSources(sources);
        vo.setUserId(userId);


        jmsTemplate.convertAndSend(RiskJmsDest.VARIABLE_EXTRACT, vo);
    }


}
