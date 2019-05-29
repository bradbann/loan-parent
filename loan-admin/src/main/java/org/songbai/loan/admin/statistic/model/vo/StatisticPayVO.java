package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class StatisticPayVO {
    private Integer agencyId;
    private String agencyName;
    private Integer vestId;
    private String vestName;
    private LocalDate payDate;//放款日期
    private Integer payCount;//放款笔数
    private String loanAmount;//借款金额
    private String payAmount;//放款金额

    private Integer firstLoanCount;//首借人数
    private String firstLoanAmount;//首借金额
    private String firstPayAmount;//首借实际放款金额

    private Integer againLoanCount;//复借人数
    private String againLoanAmount;//复借金额
    private String againPayAmount;//复借实际放款金额

    private String stampTaxAmount;//综合费
}
