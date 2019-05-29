package org.songbai.loan.schedule.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.model.config.TimedTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * 定时任务执行Job
 * 
 * @author wangd
 *
 */
@Component
public class TimedTaskJob implements Job {
	@Autowired
	JmsTemplate template;
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final TimedTaskModel taskModel = (TimedTaskModel) context.getJobDetail().getJobDataMap().get("timedTask");
		logger.info("a task {} go to start!", JSON.toJSONString(taskModel));

		template.convertAndSend(taskModel.getTaskType(), JSONObject.parseObject(taskModel.getParameters()));
	}

}
