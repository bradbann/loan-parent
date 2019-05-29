package org.songbai.loan.schedule.quartz;

import org.songbai.loan.model.config.TimedTaskModel;
import org.songbai.loan.schedule.service.TimedTaskService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.schedule.config.JobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring启动成功之后加载执行任务的配置和配置相关的任务 该类只能保证在启动时加载要执行的定时任务，在启动中后再添加或删除的执行任务此处不负责
 * 那种情况可以在添加或删除交易时间时发布有一个消息，消息响应端重新加载一遍定时任务即可
 * 
 * @author wangd
 *
 */
@Component
public class JobHandleBeanPostProcessor {

	@Autowired
	TimedTaskService timedTaskService;

	@Autowired
	JobManager jobManaager;

	private static final Logger logger = LoggerFactory.getLogger(JobHandleBeanPostProcessor.class);

	@PostConstruct
	public void postProcessAfterInitialization() {
		List<TimedTaskModel> list = timedTaskService.getAllOpenTasks();
		/**
		 * 注册所有的定时任务 所有的定时任务均为定时发布消息形式来实现
		 */
		for (final TimedTaskModel taskModel : list) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("timedTask", taskModel);
			try {
				jobManaager.addJob(taskModel.getId().toString(), TimedTaskJob.class, taskModel.getTimeTrigger(), param);
			} catch (SchedulerException e) {

				e.printStackTrace();
			}

		}

		logger.info("完成定时任务配置，配置数量" + list.size());
	}

}
