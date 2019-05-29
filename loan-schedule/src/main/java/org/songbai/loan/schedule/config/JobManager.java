package org.songbai.loan.schedule.config;

import com.alibaba.fastjson.JSON;
import org.songbai.loan.schedule.quartz.TimedTaskJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 定时任务管理器 提供定时任务的基本管理，包括定时任务的添加、和删除。 定时任务的修改相应为删除原来的定时任务然后添加新的定时任务
 *
 * @author wangd
 */
@Component
public class JobManager implements InitializingBean, DisposableBean {

    Logger logger = LoggerFactory.getLogger(JobManager.class);

    @Autowired
    TimedTaskJob timedTaskJob;
    private static String TIMEDTASK_JOB_GROUP = "TIMEDTASK_JOB_GROUP";
    private static String TIMEDTASK_TRIGGER_GROUP = "TIMEDTASK_TRIGGER_GROUP";
    private Scheduler scheduler;

    /**
     * 添加一个定时任务并启动。
     *
     * @param jobName 任务的名称，任务名称建议附带业务相关的信息，避免名称相同的情况出现
     * @param job
     * @param time    任务定时执行的时间，详情参见quartz说明文档
     * @param param
     * @throws SchedulerException
     */
    public void addJob(String jobName, Class<? extends Job> job, String time, Map<String, Object> param)
            throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob().withIdentity(jobName, TIMEDTASK_JOB_GROUP).ofType(job).build();
        if (param != null) {
            for (Entry<String, Object> entry : param.entrySet()) {
                jobDetail.getJobDataMap().put(entry.getKey(), entry.getValue());
            }
        }
        // 触发器
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, TIMEDTASK_TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(time)).build();// 触发器名,触发器组
        scheduler.setJobFactory(new JobFactory() {

            @Override
            public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

                if (bundle.getJobDetail().getJobClass().equals(TimedTaskJob.class)) {
                    return timedTaskJob;
                }
                try {
                    return bundle.getJobDetail().getJobClass().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println();
        logger.info("add a new job with id {},param is {}", jobName, JSON.toJSONString(param));
        // 启动
        if (!scheduler.isStarted()) {
            scheduler.start();
            logger.info("scheduler start!");
        }

    }

    /**
     * 根据任务id删除一个定时任务
     */
    public void deleteJob(String jobId) {

        try {
            scheduler.deleteJob(JobKey.jobKey(jobId, TIMEDTASK_JOB_GROUP));
            logger.info("delete a  time task job, id is {}", jobId);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 修改定时任务
     *
     * @param jobName
     * @param job
     * @param time
     * @param param
     * @throws SchedulerException
     */
    public void updateJob(String jobName, Class<? extends Job> job, String time, Map<String, Object> param)
            throws SchedulerException {
        logger.info("start update job with id {},param is {}", jobName, param);
        deleteJob(jobName);
        addJob(jobName, job, time, param);
        logger.info("end update job with id {},param is {}", jobName, param);
    }

    @Override
    public void destroy() throws Exception {
        scheduler.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler = new StdSchedulerFactory().getScheduler();
    }
}
