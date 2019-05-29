package org.songbai.loan.vo.sms;

import lombok.Data;

import java.util.Date;


@Data
public class SenderTemplateVO {

    /**
     * @See {SmsConst}
     */
    private String smsType; // 模板短信类型
    private String template;
    private String teleCode; // 国家编码

    private String sign; // 短信签名
    private String name;
    private Integer templateId;
    private Integer senderId;
    private Integer status;
    private String extraParam;
    private Date createTime;
    private Date updateTime;
    private Integer deleted;


}
