package org.songbai.loan.admin.product.model;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2019/1/8 3:46 PM
 */
@Data
public class ProductModelVO {
    private Integer id;
    private Integer agencyId;
    private String agencyName;
    private Integer groupId;

    private String name;//产品名称

    private Double loan;//标的金额
    private Double stamp;//综合费
    private Double pay;//基础金额

    private Integer days;//借款期限

    private Integer type;//逾期费用类型 0 固定 1按费率算
    private Integer exceedDays;//逾期计费天数
    private Double exceedFee;//逾期费用(天)
    private Double exceedRate;//逾期费率 %

    private Integer loanCountMin;//借款次数最小
    private Integer loanCountMax;//借款次数最大

    //private Integer overdueMin;//逾期天数最小
    private Integer overdueMax;//逾期天数最大

    private Integer status;// 状态 0禁用 1启用

    private String remark;//备注
    private Integer sorted;//排序
    private Integer isDefault;//1默认 0否
    private Integer deleted;//

    private Date createTime;
    private Date updateTime;
    private String groupName;

}
