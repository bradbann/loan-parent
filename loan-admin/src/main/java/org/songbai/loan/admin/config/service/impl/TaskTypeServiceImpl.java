package org.songbai.loan.admin.config.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.config.dao.TaskTypeDao;
import org.songbai.loan.admin.config.model.TaskTypeModel;
import org.songbai.loan.admin.config.service.TaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskTypeServiceImpl implements TaskTypeService {

    @Autowired
    TaskTypeDao taskTypeDao;
    @Override
    public void saveTaskTpe(TaskTypeModel taskTypeModel) {
	
    	taskTypeDao.creatTaskType(taskTypeModel);
    }

    @Override
    public List<TaskTypeModel> getAllTypes() {
	
    	return taskTypeDao.getAll();
    }

    @Override
    public void deleteByIds(String ids) {
    	taskTypeDao.deleteByIds(this.handleIds(ids));

    }

    @Override
    public Page<TaskTypeModel> querypaging(Integer pageIndex, Integer pageSize) {
		Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0 * pageSize;
		
		Integer totalCount = taskTypeDao.querypaging_count();
		List<TaskTypeModel> list = new ArrayList<>();
		if(totalCount != 0){
			list = taskTypeDao.querypaging(limit, pageSize);
		}
		Page<TaskTypeModel> page = new Page<>(pageIndex, pageSize, totalCount);
		page.setData(list);
		return page;
    }

    private List<Integer> handleIds(String ids) {
		List<Integer> result = new ArrayList<Integer>();
		String[] temp_id = ids.split(",");
		for (int i = 0; i < temp_id.length; i++) {
		    result.add(Integer.valueOf(temp_id[i]));
		}
		return result;
    }

    @Override
    public TaskTypeModel getByTaskType(String taskType) {
	
    	return taskTypeDao.getBy(null, taskType);
    }

    @Override
    public TaskTypeModel getById(Integer id) {
	
    	return taskTypeDao.getBy(id, null);
    }
}
