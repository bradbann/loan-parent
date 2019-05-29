package org.songbai.loan.schedule.jms;

import com.alibaba.fastjson.JSON;
import org.songbai.loan.model.config.TimedTaskModel;
import org.songbai.loan.schedule.config.JobManager;
import org.songbai.loan.schedule.quartz.TimedTaskJob;
import org.songbai.loan.schedule.service.TimedTaskService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TimedTaskChangedListener {

	Logger logger = LoggerFactory.getLogger(TimedTaskChangedListener.class);
	@Autowired
	TimedTaskService timedTaskService;

	@Autowired
	JobManager jobManaager;

	@JmsListener(destination = "timedTaskChangendJsm")
	public void onMessage(String message) {
		Map<String, Object> map = JSON.parseObject(message);
		String action = (String) map.get("action");
		try {
			Thread.sleep(1000);// 接收通知后1s再处理，防止对方事务未提交
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (action.equals("ADD")) {
			Integer id = (Integer) map.get("id");
			this.addOrUpdateJob(id, true);
		} else if (action.equals("DELETE")) {
			String ids = map.get("id") + "";
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				jobManaager.deleteJob(idArray[i]);
			}
		} else if (action.equals("UPDATE")) {
			Integer id = (Integer) map.get("id");
			if (id == null) {
				logger.error("id is must not null!");
				return;
			}
			this.addOrUpdateJob(id, false);
		}
	}
	
	/**
	 * 添加或修改定时任务
	 * 
	 * @param taskId
	 * @param isAdd
	 *            是否是添加定时任务 true：是；false否
	 */
	private void addOrUpdateJob(Integer taskId, boolean isAdd) {
		TimedTaskModel timedTaskModel = timedTaskService.getById(taskId);
		if (timedTaskModel == null) {
			logger.error("未能获得指定的定时任务");
			return;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("timedTask", timedTaskModel);
		try {
			if (isAdd) {
				jobManaager.addJob(timedTaskModel.getId().toString(), TimedTaskJob.class,
						timedTaskModel.getTimeTrigger(), param);
			} else {
				jobManaager.updateJob(timedTaskModel.getId().toString(), TimedTaskJob.class,
						timedTaskModel.getTimeTrigger(), param);
			}
			logger.info("update a new time task job,{}", JSON.toJSONString(timedTaskModel));
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
