package org.songbai.loan.admin.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.config.dao.SystemConfigDao;
import org.songbai.loan.admin.config.model.SystemConfigModel;
import org.songbai.loan.admin.config.service.SystemConfigPutService;
import org.songbai.loan.admin.config.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    SystemConfigDao systemConfigDao;

    @Autowired
    private SystemConfigPutService systemConfigPutService;

    @Override
    public void flushSystemConfig() throws Exception {

        SystemConfigModel deleteModel = new SystemConfigModel();
        deleteModel.setConfigKey("");
        systemConfigPutService.deleteSystemConfig(deleteModel);

        int page = 1;
        int pageSize = 1000;
        List<SystemConfigModel> list = null;
        while (true) {
            list = systemConfigDao.querypaging(null, null, (page - 1) * pageSize, pageSize);

            if (list != null && list.size() > 0) {

                for (SystemConfigModel configModel : list) {
                    systemConfigPutService.saveSystemConfig(configModel);
                }
            }
            if (list.size() < pageSize) {
                break;
            }
            list = null;
        }

    }



    @Override
    public void importConfig(String configSystem, String context) throws Exception {

        if (StringUtils.isEmpty(context)) {
            return;
        }

        String[] contextArray = StringUtils.split(context, '\n');

        if (contextArray == null || contextArray.length == 0) {
            return;
        }

        for (String s : contextArray) {

            s = StringUtils.trimToNull(s);

            if (s == null || !s.contains("=")) {
                continue;
            }
            String key = StringUtils.trimToNull(s.substring(0, s.indexOf("=")));
            String value = StringUtils.trimToNull(s.substring(s.indexOf("=") + 1));

            String config = configSystem;
            if (key.contains("||")) {
                config = StringUtils.trimToNull(key.substring(0, key.indexOf("||")));
                key = StringUtils.trimToNull(key.substring(key.indexOf("||") + 2));
            }

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                continue;
            }

            SystemConfigModel systemConfigModel = new SystemConfigModel();

            systemConfigModel.setConfigSystem(config);
            systemConfigModel.setConfigKey(key);
            systemConfigModel.setConfigValue(value);

            saveSystemConfig(systemConfigModel);
        }
    }

    @Override
    public void saveSystemConfig(SystemConfigModel systemConfigModel) throws Exception {

        SystemConfigModel oldConfigModel = getByKey(systemConfigModel.getConfigKey());

        if (oldConfigModel == null) {
            systemConfigDao.createSystemConfig(systemConfigModel);

            systemConfigPutService.saveSystemConfig(systemConfigModel);
        } else {
            updateSystemConfig(oldConfigModel.getId(),
                    oldConfigModel.getConfigKey(),
                    systemConfigModel.getConfigValue(),
                    oldConfigModel.getConfigSystem(),
                    oldConfigModel.getRemark());
        }
    }

    @Override
    public void deleteByIds(String ids) throws Exception {

        for (Integer id : this.handleIds(ids)) {
            SystemConfigModel configModel = systemConfigDao.getById(id);
            if (configModel != null) {
                systemConfigDao.deleteById(id);
                systemConfigPutService.deleteSystemConfig(configModel);

            } else {
                throw new RuntimeException("要删除的配置不能为空");
            }
        }
    }

    @Override
    public Page<SystemConfigModel> querypaging(String configKey, String configSystem, Integer pageIndex, Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0 * pageSize;

        List<SystemConfigModel> list = systemConfigDao.querypaging(configKey, configSystem, limit, pageSize);
        Integer totalCount = systemConfigDao.querypaging_count(configKey, configSystem);

        Page<SystemConfigModel> page = new Page<>(pageIndex, pageSize, totalCount);

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
    public void updateSystemConfig(Integer id, String configKey, String configValue, String remark, String configSystem) throws Exception {


        if (StringUtils.isNoneEmpty(configKey) && StringUtils.isNoneEmpty(configValue)) {
            systemConfigDao.updateSystemConfig(id, configKey, configValue, remark, configSystem);

            SystemConfigModel configModel = new SystemConfigModel();

            configModel.setConfigKey(configKey);
            configModel.setConfigValue(configValue);
            configModel.setConfigSystem(configSystem);

            systemConfigPutService.saveSystemConfig(configModel);
        }

    }

    @Override
    public SystemConfigModel getByKey(String key) {

        return systemConfigDao.getByKey(key);
    }


}
