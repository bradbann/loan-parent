package org.songbai.loan.admin.config.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 定时任务类型Model
 * @author wangd
 *
 */
public class TaskTypeModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1196592083931605295L;
    
    private Integer id;
    /**
     * 定时任务类型
     * 该类型值应该与MQ消息的名称对应
     */
    private String taskType;
    /**
     * 类型名称
     */
    private String taskName;
    /**
     * 备注
     */
    private String remark;
    private Timestamp creatDate;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTaskType() {
        return taskType;
    }
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Timestamp getCreatDate() {
        return creatDate;
    }
    public void setCreatDate(Timestamp creatDate) {
        this.creatDate = creatDate;
    }
    
    
}
