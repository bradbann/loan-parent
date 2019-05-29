package org.songbai.loan.model.sms;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
@TableName("dream_m_push_template")
public class PushTemplateModel implements Serializable {

    private Integer id;

    private Integer type;//推送类型1 公告 2借款
    private Integer subType;//子类型

    private String name;//模板名称
    private String title;//标题
    private String template;//模板内容
    private Integer isJump;//是否跳转 0不跳  1跳转

    private Date createTime;
    private Date updateTime;

}
