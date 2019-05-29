package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@TableName("dream_m_sms_template")
@Data
public class SmsTemplate implements Serializable {

    private Integer id;
    private Integer agencyId;
    private Integer vestId;
    private Integer senderId;
    private String sign;//签名
    private Integer type;//0,通用 1放款成功 2 还款提醒
    private String template;//模板内容
    private Integer status;//0停用 1 启用
    private Integer deleted;
    private Date createTime;
    private Date updateTime;


}