package org.songbai.loan.admin.config.service;


import org.songbai.loan.admin.config.model.SystemConfigModel;

/**
 * Created by yhj on 17/4/26.
 */
public interface SystemConfigPutService {


    /**
     * @param configModel
     */
    void deleteSystemConfig(SystemConfigModel configModel) throws Exception;


    /**
     * 删除所有节点
     * @throws Exception
     */
    void deleteAllSystemConfig() throws Exception;

    /**
     * @param systemConfigModel
     */
    void saveSystemConfig(SystemConfigModel systemConfigModel) throws Exception;

//    /**
//     * @param configPath
//     * @param configValue
//     * @throws Exception
//     */
//    void saveConfig(String configPath, String configValue) throws Exception;
//
//
//    /**
//     * @param configPath
//     * @param configValue
//     * @throws Exception
//     */
//    void insertConfig(String configPath, String configValue) throws Exception;
//
//    /**
//     * @param configPath
//     * @param configValue
//     * @throws Exception
//     */
//    void updateConfig(String configPath, String configValue) throws Exception;
//
//    /**
//     * @param configPath
//     * @throws Exception
//     */
//    void deleteConfig(String configPath) throws Exception;

}
