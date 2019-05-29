package org.songbai.loan.admin.activity.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Author: qmw
 * Date: 2019/1/15 11:10 AM
 */
@Data
public class ActivityModelVO {
    private Integer id;
    private Integer agencyId;
    private String agencyName;
    private String name;//活动名称
    private String code;//活动code
    private String picture;//图片地址
    private String url;//图片地址

    private Integer status;// 状态 0禁用 1启用
    private Integer deleted;// 0未删除1 已删除
    private String remark;// 备注

    private List<Integer> scopes; // 范围。
    private List<Integer> vestlist ; // 马甲包ID

    private Date createTime;//
    private Date updateTime;//
}
