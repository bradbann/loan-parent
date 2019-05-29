package org.songbai.loan.model.sms;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SmsSenderTemplate implements Serializable {

    private Integer id;
    private Integer agencyId;
    private String name;
    private String sign; // 短信签名
    private Integer templateId;
    private Integer senderId;
    private Integer status;
    private String extraParam;
    private Date createTime;
    private Date updateTime;
    private Integer deleted;
    private Integer vestId;


}
