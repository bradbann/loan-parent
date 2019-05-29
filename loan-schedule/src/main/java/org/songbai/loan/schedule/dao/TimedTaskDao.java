package org.songbai.loan.schedule.dao;



import org.songbai.loan.model.config.TimedTaskModel;

import java.util.List;


public interface TimedTaskDao {

    /**
     * 获得所有开启的定时任务
     * @return
     */
    public List<TimedTaskModel> getAllOpenTask();
    
    public TimedTaskModel getById(Integer id);
}
