package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("dream_m_sms_sender")
public class SmsSender implements Serializable {

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

}
