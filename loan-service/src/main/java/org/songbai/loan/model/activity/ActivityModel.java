package org.songbai.loan.model.activity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/12/17 11:44 AM
 */
@TableName("loan_u_activity")
@Data
public class ActivityModel implements Serializable {
    private Integer id;
    private Integer agencyId;
    private String name;//活动名称
    private String code;//活动code
    private String picture;//图片地址
    private String url;//图片地址

    private Integer status;// 状态 0禁用 1启用
    private Integer deleted;// 0未删除1 已删除
    private String remark;// 备注

    private String scopes; // 范围。
    private String vestlist ; // 马甲包ID

    private Date createTime;//
    private Date updateTime;//
}
