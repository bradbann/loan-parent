package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 8:46 PM
 */
@Data
public class OrderRepayVO {
    private String orderNumber;//订单号

    private String username;//用户昵称
    private String userPhone;//用户手机号
    private Integer days;//借款期限

    private Integer status;//
    private Double loan;//借款金额

    private Integer exceedDays;//逾期天数
    private String exceedFee;//逾期费用

    private String payment;//应还金额
    private String alreadyMoney;//已还金额
    private String deductMoney;//减免金额


    private String bankName;// 银卡昵称
    private String branchBank;// 支行名称
    private String bankNumber;// 卡号

    private Date transferTime;//到账时间
    private Date repaymentDate;//还款日期

    private String remark;// 备注
    Integer agencyId;
    String agencyName;
    String vestName;
    Integer vestId;

}
