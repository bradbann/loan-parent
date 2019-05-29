package org.songbai.loan.admin.sms.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Author: qmw
 * Date: 2019/1/7 3:14 PM
 */
@Data
public class SmsSenderVO {
    private Integer id;
    private Integer agencyId;
    private String name;
    private String account;
    private String password;
    private Integer status;
    /**
     * @see org.songbai.loan.constant.sms.SmsConstant
     */
    private Integer type;//模板类型 目前只有5 6 泡泡云
    private String data;//短信配置其他参数
    private Integer isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String agencyName;

}
