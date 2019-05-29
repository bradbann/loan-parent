package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("dream_m_email_template")
public class EmailTemplateModel {

    private Integer id;
    private String name;
    /**
     * @See {SmsConst}
     */

    private Integer type; // 模板类型
    private String template;

    private String teleCode; // 国家电话编码

    private Integer deleted;

    private Date createTime;
    private Date updateTime;
    private String agencyCode;

}
