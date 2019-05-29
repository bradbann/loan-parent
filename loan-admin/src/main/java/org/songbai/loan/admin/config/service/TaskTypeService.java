package org.songbai.loan.admin.config.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.config.model.TaskTypeModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author wangd
 *
 */
@Component
public interface TaskTypeService {

    /**
     * 保存任务类型
     * @param taskTypeModel
     */
    public void saveTaskTpe(TaskTypeModel taskTypeModel);
    /**
     * 获得所有的任务类型
     * @return
     */
    public List<TaskTypeModel> getAllTypes();
    
    public void deleteByIds(String ids);
    /**
     * 分页获得任务类型
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<TaskTypeModel> querypaging(Integer pageIndex, Integer pageSize);
    
    public TaskTypeModel getByTaskType(String taskType);

    public TaskTypeModel getById(Integer id);
}
