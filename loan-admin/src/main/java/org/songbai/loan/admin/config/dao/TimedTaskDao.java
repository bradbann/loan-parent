package org.songbai.loan.admin.config.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.config.TimedTaskModel;

import java.util.List;

/**
 * 定时任务Dao
 *
 * @author wangd
 */
public interface TimedTaskDao {

	public void createTimedTask(TimedTaskModel timedTaskModel);

	public void updateTimedTask(TimedTaskModel timedTaskModel);

	public void deleteByIds(@Param(value = "ids") List<Integer> ids);

	/**
	 * 获得所有开启的定时任务
	 *
	 * @return
	 */
	public List<TimedTaskModel> getAllOpenTask();

	/**
	 * 根据任务的id批量修改定时任务的开启或关闭
	 *
	 * @param ids
	 * @param isOpen
	 *            定时任务开启是否开启 true 开启；false： 关闭
	 */
	public void changeIsOpenBIds(@Param(value = "ids") List<Integer> ids, @Param(value = "isOpen") boolean isOpen);

	public List<TimedTaskModel> querypaging(@Param(value = "typeId") Integer typeId,
                                            @Param(value = "isOpen") Boolean isOpen, @Param(value = "parameters") String parameters, @Param(value = "remark") String remark, @Param(value = "limit") Integer limit,
                                            @Param(value = "size") Integer size);

	public Integer querypaging_count(@Param(value = "typeId") Integer typeId, @Param(value = "isOpen") Boolean isOpen, @Param(value = "parameters") String parameters, @Param(value = "remark") String remark);

	int updateTimedByExchangeIdAndTopChannelId(TimedTaskModel timedTaskModel);

	TimedTaskModel findTimedTaskIdByIds(@Param("exchangeId") Integer exchangeId,
                                        @Param("topChannelId") Integer topChannelId, @Param("topTimeTaskId") Long topTimeTaskId);

	int deleteByExchangeIdAndTopChannelId(@Param("exchangeId") Integer exchangeId,
                                          @Param("topChannelId") Integer topChannelId, @Param("topTimeTaskId") Long topTimeTaskId);

	String getPushTaskByIds(@Param("ids") String ids, @Param("taskType") String taskType);

	TimedTaskModel findOlderPushTaskByParam(@Param("id") Integer id, @Param("taskType") String taskType);

	TimedTaskModel getScheduleByParamter(String paramter);

}
