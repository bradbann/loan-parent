package org.songbai.loan.admin.admin.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.config.model.TaskTypeModel;
import org.songbai.loan.admin.config.service.TaskTypeService;
import org.songbai.loan.admin.config.service.TimedTaskService;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.config.TimedTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/timedTask")
public class TimedTaskController {

    private static Logger logger = LoggerFactory.getLogger(TimedTaskController.class);

    @Autowired
    TimedTaskService timedTaskService;

    @Autowired
    TaskTypeService taskTypeService;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping(value = "/saveTimedTask")
    @ResponseBody
    public Response saveTimedTask(Integer typeId, String timePeriod, String timeTrigger, String parameters,
                                  String remark) {

        Assert.notNull(typeId, "定时任务类型不能为空");
        Assert.notNull(timeTrigger, "任务执行时间不能为空");

        TaskTypeModel taskTypeModel = taskTypeService.getById(typeId);

        if (taskTypeModel != null) {
            TimedTaskModel timedTaskModel = new TimedTaskModel();

            timedTaskModel.setTypeId(typeId);
            timedTaskModel.setTaskType(taskTypeModel.getTaskType());
            timedTaskModel.setTypeName(taskTypeModel.getTaskName());
            timedTaskModel.setTimePeriod(timePeriod);
            timedTaskModel.setTimeTrigger(timeTrigger);
            timedTaskModel.setParameters(parameters);
            timedTaskModel.setRemark(remark);
            timedTaskModel.setIsOpen(true);
            timedTaskModel.setCreateDate(new Timestamp(System.currentTimeMillis()));

            timedTaskService.saveTiemdTask(timedTaskModel);
            Map<String, Object> message = new HashMap<String, Object>();
            message.put("action", "ADD");
            message.put("id", timedTaskModel.getId());
//			timedTaskChangendSender.sendMessage(JSON.toJSONString(message));
            jmsMessagingTemplate.convertAndSend(JmsDest.SCHEDULE_TIMED_TASK_CHANGEND, JSON.toJSONString(message));
        } else {
            return Response.error("保存失败，未能找到指定的任务类型");
        }

        return Response.success();
    }

    @RequestMapping(value = "/updateTimedTask")
    @ResponseBody
    public Response updateTimedTask(Integer id, String timePeriod, String timeTrigger, String parameters, String remark,
                                    HttpServletRequest request) {

        Assert.notNull(id, "要修改的定时任务id不能为空");
        Assert.notNull(timeTrigger, "任务执行时间不能为空");
        logger.info("修改定时任务：id={},userId={}", id, request.getAttribute("userId"));
        TimedTaskModel timedTaskModel = new TimedTaskModel();
        timedTaskModel.setTimePeriod(timePeriod);
        timedTaskModel.setTimeTrigger(timeTrigger);
        timedTaskModel.setParameters(parameters);
        timedTaskModel.setRemark(remark);

        timedTaskService.updateTiemdTask(timedTaskModel);
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("action", "UPDATE");
        message.put("id", timedTaskModel.getId());
//		timedTaskChangendSender.sendMessage(JSON.toJSONString(message));
        jmsMessagingTemplate.convertAndSend(JmsDest.SCHEDULE_TIMED_TASK_CHANGEND, JSON.toJSONString(message));
        return Response.success();
    }

    /**
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteTimedTask")
    @ResponseBody
    public Response deleteTimedTask(String ids, HttpServletRequest request) {
        Assert.notNull(ids, "请求参数出错");
        logger.info("删除定时任务：ids={},userId={}", ids, request.getAttribute("userId"));
        timedTaskService.deleteByIds(ids);
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("action", "DELETE");
        message.put("id", ids);
//		timedTaskChangendSender.sendMessage(JSON.toJSONString(message));
        jmsMessagingTemplate.convertAndSend(JmsDest.SCHEDULE_TIMED_TASK_CHANGEND, JSON.toJSONString(message));
        return Response.success();
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/querypaging")
    @ResponseBody
    public Response querypaging(Integer typeId, Boolean isOpen, String parameters, String remark, Integer page, Integer pageSize) {

        Page<TimedTaskModel> pageTaskType = timedTaskService.querypaging(typeId, isOpen, parameters, remark, page, pageSize);
        return Response.success(pageTaskType);
    }

    @RequestMapping(value = "/instantlyExecute")
    @ResponseBody
    public Response instantlyExecute(Integer taskId, String taskType, String parameters) {
        Assert.notNull(taskId, "请求参数出错");
        Assert.notNull(taskType, "请求参数出错");
        timedTaskService.instantlyExecute(taskId, taskType, parameters);
        return Response.success();
    }
}
