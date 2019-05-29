package org.songbai.loan.admin.sms.model;

import lombok.Data;

import java.sql.Date;

@Data
public class SmsSenderTemplateVO  {
    private Integer id;
    private String agencyName;
    private Integer vestId;
    private Integer agencyId;
    private String vestName;
    private Integer senderId;
    private String senderName;
    private String sign;//签名
    private Integer type;//0,通用 1放款成功 2 还款提醒
    private String template;//模板内容
    private Integer general;//是否为通用验证码  0否
    private Integer status;//0停用 1 启用
    private Integer deleted;
    private Date createTime;
    private Date updateTime;

}
