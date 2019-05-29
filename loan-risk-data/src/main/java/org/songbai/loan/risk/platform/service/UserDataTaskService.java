package org.songbai.loan.risk.platform.service;

import org.songbai.loan.constant.risk.RiskConst;

public interface UserDataTaskService {


    void saveDataTask(String userid, String source, String taskId, RiskConst.Task status, String remark);

    void saveDataTaskForMoxie(String body, String eventName, String eventType, RiskConst.Task status);
}
