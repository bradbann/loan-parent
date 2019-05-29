package org.songbai.loan.admin.finance.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DeductQueueVo{

    private String orderNumber; // 订单号码
    private String vestName;
    private String userName;
    private String userPhone;

    private Double payment;//应还金额
    private Double deductMoney;// 已经扣款金额
    private Integer status; // 1:等待扣款， 2:扣款中， 3: 扣款结束，4:异常终止
    private Integer deductNum; // 扣款次数

    private Date repaymentDate;//还款日期

    private String remark;  // 备注
    private Integer vestId;
    private Date createTime;

}
