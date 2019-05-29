package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class StatisticRepayVO {
    private Integer agencyId;
    private String agencyName;
    private Integer vestId;
    private String vestName;

    private LocalDate repayDate;//应还日期

    private Integer orderCount;//应还订单数量
    private Integer earlyCount;//提前还款数量
    private Integer normalCount;//正常还款数量
    private Integer overdueRepayCount;//逾期还款数量

    private Integer overduePayCount;//逾期中数量
    private Integer repayFailCount;//坏账数量

    private String repayAmount;//应还金额
    private String realRepayAmount;//实还金额
    private String payAmount;//放款成本金额
    private String exceedAmount;//逾期金额
    private String deductAmount;//减免金额
    private String leftAmount;//待还金额

    private String firstOverdueRate = "0.00";//首逾率
    private String overdueRate = "0.00";//逾期率

    private String chaseOneRate = "0.00";//回收1天
    private String chaseThreeRate = "0.00";//回收3天
    private String chaseSevenRate = "0.00";//回收7天
    private String chaseFifteenRate = "0.00";//回收15天
}
