package org.songbai.loan.admin.config.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.config.model.TaskTypeModel;

import java.util.List;

/**
 * 定时任务类型Dao
 *
 * @author wangd
 */
public interface TaskTypeDao {

    public void creatTaskType(TaskTypeModel taskTypeModel);

    public List<TaskTypeModel> getAll();

    public void deleteByIds(@Param(value = "ids") List<Integer> ids);

    public List<TaskTypeModel> querypaging(@Param(value = "limit") Integer limit, @Param(value = "size") Integer size);

    public Integer querypaging_count();

    /**
     * 根据任务类型获得任务类型对象
     *
     * @param taskType
     * @return
     */
    public TaskTypeModel getBy(@Param(value = "id") Integer id, @Param(value = "taskType") String taskType);

    TaskTypeModel findCountTaskTypeByTaskType(String taskType);
}
