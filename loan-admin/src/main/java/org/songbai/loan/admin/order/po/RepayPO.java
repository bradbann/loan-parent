package org.songbai.loan.admin.order.po;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/8 1:41 PM
 */
@Data
public class RepayPO extends OptPO{
    private String orderNumber;//单号
    private Double payment;//应还金额
    //private Double deductMoney;//减免金额
    private String repayType;//还款方式
    private String receipt;//还款凭证
    private Date repaymentTime;// 还款时间
    private String remark;//
}
