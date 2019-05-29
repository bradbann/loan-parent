package org.songbai.loan.admin.config.service.impl;

import com.alibaba.fastjson.JSON;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.config.dao.TimedTaskDao;
import org.songbai.loan.admin.config.service.TimedTaskService;
import org.songbai.loan.model.config.TimedTaskModel;
import org.songbai.loan.model.config.TimedTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TimedTaskServiceImpl implements TimedTaskService {

	private static final String QUEUE_SUFFIX = ".instantly";

	@Autowired
    JmsTemplate jmsTemplate;

	@Autowired
	TimedTaskDao timedTaskDao;

	@Override
	public void saveTiemdTask(TimedTaskModel taskModel) {
		timedTaskDao.createTimedTask(taskModel);

	}

	@Override
	public void deleteByIds(String ids) {
		timedTaskDao.deleteByIds(this.handleIds(ids));

	}

	@Override
	public List<TimedTaskModel> getAllOpenTasks() {

		return timedTaskDao.getAllOpenTask();
	}

	@Override
	public void changeIsOpenByIds(String ids, Boolean isOpen) {
		timedTaskDao.changeIsOpenBIds(this.handleIds(ids), isOpen);

	}

	@Override
	public Page<TimedTaskModel> querypaging(Integer typeId, Boolean isOpen, String parameters,String remark ,Integer pageIndex, Integer pageSize) {
		Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0 * pageSize;

		Integer totalCount = timedTaskDao.querypaging_count(typeId, isOpen,parameters,remark);
		List<TimedTaskModel> list = new ArrayList<>();
		if(totalCount != null){
			list = timedTaskDao.querypaging(typeId, isOpen,parameters,remark, limit, pageSize);
		}
		Page<TimedTaskModel> page = new Page<>(pageIndex, pageSize, totalCount);
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
	public void updateTiemdTask(TimedTaskModel taskModel) {
		timedTaskDao.updateTimedTask(taskModel);
	}

	@Override
	public void instantlyExecute(int taskId, String taskType, String parameters) {

		sendTradeMessage(taskId, taskType, parameters);
	}

	public void sendTradeMessage(final int taskId, String queueName, final String parameters) {

		jmsTemplate.convertAndSend(queueName, JSON.parseObject(parameters));

	}
}
