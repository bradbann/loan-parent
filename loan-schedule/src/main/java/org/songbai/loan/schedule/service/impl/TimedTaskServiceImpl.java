package org.songbai.loan.schedule.service.impl;

import org.songbai.loan.model.config.TimedTaskModel;
import org.songbai.loan.schedule.service.TimedTaskService;
import org.songbai.loan.schedule.dao.TimedTaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimedTaskServiceImpl implements TimedTaskService {
	

    @Autowired
    TimedTaskDao timedTaskDao;

    @Override
    public List<TimedTaskModel> getAllOpenTasks() {

    	return timedTaskDao.getAllOpenTask();
    }

    @Override
    public TimedTaskModel getById(Integer id) {
	
    	return timedTaskDao.getById(id);
    }
    
}
