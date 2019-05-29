package org.songbai.loan.admin.config.service;


import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.config.model.SystemConfigModel;

public interface SystemConfigService {

    /**
     * 刷新所有的配置
     */
    public void flushSystemConfig() throws Exception;

    /**
     * 导入
     * @param configSystem
     * @param context
     */
    public void importConfig(String configSystem, String context) throws Exception;

    public void saveSystemConfig(SystemConfigModel systemConfigModel) throws Exception;

    public void deleteByIds(String ids) throws Exception;

    public Page<SystemConfigModel> querypaging(String configKey, String configSystem, Integer pageIndex, Integer pageSize);

    public void updateSystemConfig(Integer id, String configKey, String configValue, String remark, String configSystem)
            throws Exception;

    /**
     * 根据配置 的名称获得对象， 配置的名称即key是唯一的，保存之前必须要做验证
     *
     * @param key
     * @return
     */
    public SystemConfigModel getByKey(String key);
}
