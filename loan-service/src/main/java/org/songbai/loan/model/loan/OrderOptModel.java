package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/10/30 下午4:36
 */
@Data
@TableName("loan_u_order_opt")
public class OrderOptModel {
    private Integer id;
    private Integer userId;//用户id
    private Integer agencyId;//代理id
    private Integer actorId;//操作人id
    private Integer type;//1 机审 2 人审

    private String orderNumber;//订单号
    private Integer stage;//阶段
    private String stageFlag;//阶段名称
    private Integer status;//0失败 1成功
    private String remark;//备注
    private Integer guest;//客群

    private Date createTime;//操作时间
    private Date orderTime;//订单创建时间

}
