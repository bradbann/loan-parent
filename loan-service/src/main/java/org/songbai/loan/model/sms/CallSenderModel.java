package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
@TableName("dream_m_call_sender")
public class CallSenderModel implements Serializable {

    private Integer id;
    private Integer agencyId;
    private Integer vestId;
    private String name;

    private String appId;
    private String appKey;
    private String data;//外呼配置其他参数
    private String url;//api访问地址


    private Integer type;//提供商 1深市智能
    private Integer status;
    private String remark;

    private Integer isDelete;
    private Date createTime;
    private Date updateTime;

}
