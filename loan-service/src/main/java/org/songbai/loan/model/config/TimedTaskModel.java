package org.songbai.loan.model.config;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 定时任务Model
 *
 * @author wangd
 */

public class TimedTaskModel implements Serializable {
    private static final long serialVersionUID = -7174906227453238456L;
    
    private Integer id;
    
    private Integer typeId;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 时间段
     */
    private String timePeriod;
    /**
     * 任务执行的时间表达式
     */
    private String timeTrigger;
    /**
     * 是否开启该任务
     */
    private Boolean isOpen = true;
    /**
     * 任务相关参数，参数为标准JSON格式字符串
     */
    private String parameters;
    
    private String remark;
    
    private Timestamp createDate;
    private Integer exchangeTimeId;
    private Integer exchangeId;
    public Integer getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Integer exchangeId) {
		this.exchangeId = exchangeId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTimeTrigger() {
        return timeTrigger;
    }

    public void setTimeTrigger(String timeTrigger) {
        this.timeTrigger = timeTrigger;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public Integer getExchangeTimeId() {
		return exchangeTimeId;
	}

	public void setExchangeTimeId(Integer exchangeTimeId) {
		this.exchangeTimeId = exchangeTimeId;
	}
    
    
}
