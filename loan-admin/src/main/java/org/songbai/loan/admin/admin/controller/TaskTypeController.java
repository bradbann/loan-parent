package org.songbai.loan.admin.admin.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.config.model.TaskTypeModel;
import org.songbai.loan.admin.config.service.TaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/taskType")
public class TaskTypeController {

    @Autowired
    TaskTypeService taskTypeService;

    @RequestMapping(value = "/saveTaskType")
    @ResponseBody
    public Response saveTaskType(String taskType, String taskName, String remark) {

        Assert.notNull(taskType, "定时任务类型不能为空");

        if (taskTypeService.getByTaskType(taskType) != null) {
            return Response.error("保存失败，任务类型冲突");
        }
        TaskTypeModel taskTypeModel = new TaskTypeModel();

        taskTypeModel.setTaskType(taskType);
        taskTypeModel.setTaskName(taskName);
        taskTypeModel.setRemark(remark);

        taskTypeService.saveTaskTpe(taskTypeModel);
        return Response.success();
    }

    /**
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteTaskType")
    @ResponseBody
    public Response deleteTaskType(String ids) {
        Assert.notNull(ids, "要删除的假期配置id不能为空");
        taskTypeService.deleteByIds(ids);
        return Response.success();
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/querypaging")
    @ResponseBody
    public Response querypaging(Integer page, Integer pageSize) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        Page<TaskTypeModel> pageTaskType = taskTypeService.querypaging(page, pageSize);
        return Response.success(pageTaskType);
    }

    /**
     * 查询全部的任务类型 主要是为来下拉的数据
     */
    @RequestMapping(value = "/getAllTaskType")
    @ResponseBody
    public Response getAllTaskTpe() {

        return Response.success(taskTypeService.getAllTypes());
    }
}
