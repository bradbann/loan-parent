package org.songbai.loan.risk.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.model.user.RiskUserDataTaskModel;
import org.songbai.loan.risk.platform.dao.UserDataTaskDao;
import org.songbai.loan.risk.platform.service.UserDataTaskService;
import org.songbai.loan.vo.risk.TaskNotifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDataTaskServiceImpl implements UserDataTaskService {


    @Autowired
    UserDataTaskDao dataTaskDao;

    @Autowired
    JmsTemplate jmsTemplate;

    @Override
    public void saveDataTask(String userid, String source, String taskId, RiskConst.Task status, String remark) {
        RiskUserDataTaskModel model = new RiskUserDataTaskModel();

        model.setUserId(userid);
        model.setSources(source);

        RiskUserDataTaskModel oldModel = dataTaskDao.selectOne(model);

        model.setTaskId(taskId);
        model.setStatus(status.code);
        model.setRemark(remark);

        if (oldModel == null) {
            dataTaskDao.insert(model);
        } else {
            model.setId(oldModel.getId());
            dataTaskDao.updateById(model);
        }
    }


    @Override
    public void saveDataTaskForMoxie(String body, String eventName, String eventType, RiskConst.Task status) {

        JSONObject map = JSONObject.parseObject(body);

        String userId = map.getString("user_id");
        String taskId = map.getString("task_id");

        String sources = "";


        switch (eventType.toLowerCase()) {
            case "taobao":
                if (eventName.equalsIgnoreCase("report")) {
                    sources = VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT;
                } else {
                    sources = VariableConst.VAR_SOURCE_MOXIE_TAOBAO;
                }
                break;
            case "carrier":
                if (eventName.equalsIgnoreCase("report")) {
                    sources = VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT;
                } else {
                    sources = VariableConst.VAR_SOURCE_MOXIE_CARRIER;
                }
                break;
        }

        saveDataTask(userId, sources, taskId, status, map.getString("message"));


        TaskNotifyVO notifyVO = TaskNotifyVO.builder()
                .sources(sources)
                .taskId(taskId)
                .userId(userId)
                .status(status.code).build();


        log.info("notify user update user status :{}", notifyVO);

        jmsTemplate.convertAndSend(JmsDest.RISK_DATA_NOTIFY, JSONObject.toJSONString(notifyVO));
    }
}
