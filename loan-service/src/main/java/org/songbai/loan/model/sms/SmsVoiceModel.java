package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@TableName("dream_m_voice_sender")
@Data
public class SmsVoiceModel implements Serializable {

    private Integer id;
    private String name;//厂商名称
    private Integer agencyId;
    private Integer type;//短信语音服务商类型 目前只有1 螺丝帽

    private Integer status;
    private String data;//短信配置其他参数
    private String url;//接入地址

    private Integer isDelete;
    private Date createTime;
    private Date updateTime;


}