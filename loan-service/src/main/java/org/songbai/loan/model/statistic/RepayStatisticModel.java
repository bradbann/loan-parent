package org.songbai.loan.model.statistic;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/24 3:19 PM
 * 放款统计
 */
@Data
@TableName("loan_s_repay")
public class RepayStatisticModel {
    private Integer id;
    private Integer agencyId;
    private Integer vestId;

    private LocalDate repayDate;//应还日期

    private Integer orderCount;//应还订单数量
    private Integer earlyCount;//提前还款数量
    private Integer normalCount;//正常还款数量
    private Integer overdueRepayCount;//逾期还款数量

    private Integer overduePayCount;//逾期中数量
    private Integer repayFailCount;//坏账数量

    private Double repayAmount;//应还金额
    private Double realRepayAmount;//实还金额
    private Double payAmount;//放款成本金额
    private Double exceedAmount;//逾期金额
    private Double deductAmount;//减免金额
    private Double leftAmount;//待还金额

    private Integer firstOverdueCount;//逾期数量
    private Integer overdueCount;//逾期数量(没用)

    private Integer chaseOneCount;//回收1天
    private Integer chaseThreeCount;//回收3天
    private Integer chaseSevenCount;//回收7天
    private Integer chaseFifteenCount;//回收15天

    private Date createTime;
    private Date updateTime;
}
