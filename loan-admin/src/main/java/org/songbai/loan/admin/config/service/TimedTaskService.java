package org.songbai.loan.admin.config.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.model.config.TimedTaskModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务管理Service
 * 
 * @author wangd
 *
 */
@Component
public interface TimedTaskService {

	public void saveTiemdTask(TimedTaskModel taskModel);

	public void updateTiemdTask(TimedTaskModel taskModel);

	public void deleteByIds(String ids);

	public List<TimedTaskModel> getAllOpenTasks();

	public void changeIsOpenByIds(String ids, Boolean isOpen);

	public Page<TimedTaskModel> querypaging(Integer typeId, Boolean isOpen, String parameters, String remark, Integer pageIndex, Integer pageSize);

	public void instantlyExecute(int taskId, String taskType, String parameters);
}
