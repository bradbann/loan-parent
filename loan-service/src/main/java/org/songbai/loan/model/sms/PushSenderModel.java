package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
@TableName("dream_m_push_sender")
public class PushSenderModel implements Serializable {
    private Integer id;
    private Integer agencyId;
    private String name;
    private Integer type; //推送平台 1.个推
    private String appId;
    private String appKey;
    private String master;
    private String url;//个推使用
    private Integer status; //0停用 1启用
    private Integer deleted;
    private Date createTime;
    private Date updateTime;

}
