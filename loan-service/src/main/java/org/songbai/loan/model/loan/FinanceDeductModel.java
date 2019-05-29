package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_finance_deduct")
public class FinanceDeductModel {

    private Integer id;
    private Integer userId;
    private Integer agencyId;
    private Integer orderId; // 订单Id
    private String orderNumber; // 订单号码
    private Double payment;//应还金额

    private Double deductMoney;// 已经扣款金额
    private Integer deductNum; // 扣款次数
    private Integer status; // 1:等待扣款， 2:扣款中， 3: 扣款结束，4:异常终止
    private String remark;  // 备注

    private Integer deductType ; // 扣款方式 {@link DeductConst.DeductType}  1:按照比例扣款，2:固定额度扣款
    private String deductConfig ; // 1: 50:25:15 ,2:300



    private Integer actorId;//操作人id
    private String actorName; // 提交人

    private Date createTime; //
    private Date updateTime;



}
