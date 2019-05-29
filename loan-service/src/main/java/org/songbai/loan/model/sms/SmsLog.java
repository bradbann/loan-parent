package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;



@Data
@TableName("dream_m_sms_log")
public class SmsLog implements Serializable {
    private static final long serialVersionUID = -9076968437695565916L;

    @TableId
    private Integer id;

    private String ip;
    private String tele;
    private String sign;
    private String temlate;
    private Integer type;
    private String extraParam;
    private String param;
    private String content;

    private Date createTime;


    private Integer senderId;
    private Integer templateId;
    private Integer status; // 1 成功， 0 失败。
    private String remark; //失败备注

}
