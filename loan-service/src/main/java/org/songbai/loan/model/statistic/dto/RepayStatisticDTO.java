package org.songbai.loan.model.statistic.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/24 3:19 PM
 * 放款统计
 */
@Data
public class RepayStatisticDTO {

    private Integer agencyId;
    private Integer vestId;

    private LocalDate repayDate;//还款日期

    private Integer isEarly = 0;//是否提前还款
    private Integer isNormal = 0;//是否正常还款
    private Integer isOverdue = 0;//是否逾期还款

    private Integer isOnOverdue = 0;//是否逾期
    private Integer isOnFirstOverdue = 0;//是否首次逾期
    private Integer isFail = 0;//是否坏账

    private Double repayMoney = 0D;//还款金额
    private Double deductMoney = 0D;//减免金额
    private Double overdueMoney = 0D;//逾期金额

    private Integer isOneOverdue = 0;//回收1天
    private Integer isThreeOverdue = 0;//回收3天
    private Integer isSevenOverdue = 0;//回收5天
    private Integer isFifteenOverdue = 0;//回收15天
    private Integer isFinish = 0;//是否全部还款完成
}
