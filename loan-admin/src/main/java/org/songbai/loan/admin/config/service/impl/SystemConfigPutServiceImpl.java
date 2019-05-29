package org.songbai.loan.admin.config.service.impl;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.admin.config.model.SystemConfigModel;
import org.songbai.loan.admin.config.service.SystemConfigPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yhj on 17/4/26.
 */
@Service
public class SystemConfigPutServiceImpl implements SystemConfigPutService {

    private static Logger logger = LoggerFactory.getLogger(SystemConfigPutServiceImpl.class);

    @Autowired
    private SpringProperties springProperties;


    @Autowired
    CuratorFramework curatorFramework;


    @Override
    public void deleteSystemConfig(SystemConfigModel configModel) throws Exception {
        String path = getConfigPath(configModel);
        deleteConfig(path);
    }

    @Override
    public void deleteAllSystemConfig() throws Exception {
        String path = getConfigPath(null);
        // 删除所有的子节点
        curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
    }


    @Override
    public void saveSystemConfig(SystemConfigModel systemConfigModel) throws Exception {
        String path = getConfigPath(systemConfigModel);
        saveConfig(path, systemConfigModel.getConfigValue());
    }


    public void deleteConfig(String configPath) throws Exception {

        if (this.checkedHasNode(configPath)) {
            curatorFramework.delete().forPath(configPath);
        }
    }


    public void saveConfig(String configPath, String configValue) throws Exception {
        if (checkedHasNode(configPath)) {
            updateConfig(configPath, configValue);
        } else {
            insertConfig(configPath, configValue);
        }
    }


    public void insertConfig(String configPath, String configValue) throws Exception {

        try {
            curatorFramework.create().creatingParentsIfNeeded().forPath(configPath, configValue.getBytes());
        } catch (Exception e) {
            logger.warn("insert zookeeper config for configPath[{}] , configValue[{}]", configPath, configValue);
            throw e;
        }
    }


    public void updateConfig(String configPath, String configValue) throws Exception {


        try {
            curatorFramework.setData().forPath(configPath, configValue.getBytes());
        } catch (Exception e) {
            logger.warn("update zookeeper config for configPath[{}] , configValue[{}]", configPath, configValue);
            throw e;
        }
    }


    private String getConfigPath(SystemConfigModel systemConfigModel) {
        String root = springProperties.getString("config.center.zkRoot", "/sys_conf");


        String key = "";
        if (systemConfigModel != null) {

//            key = key + "/" + systemConfigModel.getConfigSystem();

            if (systemConfigModel.getConfigKey().startsWith("/")) {
                key = key + systemConfigModel.getConfigKey();
            } else {
                key = key + "/" + systemConfigModel.getConfigKey();
            }
        }


        return root + key;
    }

    /**
     * 根据path验证该节点配置是否已经存在
     * 在添加和删除之前通过此方法验证一下操作的节点是否存在，这样可以减少添加和删除失败的情况。
     *
     * @param
     * @return
     */
    private boolean checkedHasNode(String path) {

        try {
            curatorFramework.getData().forPath(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
