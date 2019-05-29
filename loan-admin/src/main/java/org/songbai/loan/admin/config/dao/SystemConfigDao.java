package org.songbai.loan.admin.config.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.config.model.SystemConfigModel;

import java.util.List;

public interface SystemConfigDao {

	public void createSystemConfig(SystemConfigModel systemConfigModel);

	public void deleteByIds(@Param(value = "ids") List<Integer> ids);

	public List<SystemConfigModel> querypaging(@Param(value = "configKey") String configKey, @Param(value = "configSystem") String configSystem, @Param(value = "limit") Integer limit,
                                               @Param(value = "size") Integer size);

	public Integer querypaging_count(@Param(value = "configKey") String configKey, @Param(value = "configSystem") String configSystem);

	/**
	 * 修改
	 *
	 * @param id
	 *            要修改的纪录的id，不能为空
	 * @param configKey
	 * @param configValue
	 * @param remark
	 */
	public void updateSystemConfig(@Param(value = "id") Integer id, @Param(value = "configKey") String configKey,
                                   @Param(value = "configValue") String configValue, @Param(value = "remark") String remark,
                                   @Param(value = "configSystem") String configSystem);

	public void deleteById(@Param(value = "id") Integer id);

	public SystemConfigModel getById(@Param(value = "id") Integer id);

	public SystemConfigModel getByKey(@Param(value = "key") String key);
}
