package org.songbai.loan.admin.config.model;

import java.io.Serializable;

/**
 * 系统配置
 * @author wangd
 *
 */
public class SystemConfigModel implements Serializable {

    private static final long serialVersionUID = 5102083533164729825L;
    
    private Integer id;
    
    private String configKey;
    
    private String configValue;
    
    private String remark;
    
    private String configSystem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getConfigSystem() {
		return configSystem;
	}

	public void setConfigSystem(String configSystem) {
		this.configSystem = configSystem;
	}
    
    
}
