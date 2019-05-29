package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/10/30 下午4:36
 */
@Data
@TableName("loan_u_product_group")
public class ProductGroupModel {
    private Integer id;
    private Integer agencyId;

    private String name;//分组名称

    private Integer status;// 状态 0禁用 1启用

    private String remark;//备注
    private Integer deleted;//

    private Date createTime;
    private Date updateTime;

}
