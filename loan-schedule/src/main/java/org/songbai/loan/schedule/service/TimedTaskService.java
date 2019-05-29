package org.songbai.loan.schedule.service;

import org.songbai.loan.model.config.TimedTaskModel;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 定时任务管理Service
 * @author wangd
 *
 */
@Component
public interface TimedTaskService {

    public List<TimedTaskModel> getAllOpenTasks();
    
    public TimedTaskModel getById(Integer id);
}
