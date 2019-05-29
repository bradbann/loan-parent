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
@TableName("loan_s_pay")
public class PayStatisticModel {
    private Integer id;
    private Integer agencyId;
    private Integer vestId;
    private LocalDate payDate;//放款日期
    private Integer payCount;//放款笔数
    private Double loanAmount;//借款金额
    private Double payAmount;//放款金额

    private Integer firstLoanCount;//首借人数
    private Double firstLoanAmount;//首借金额
    private Double firstPayAmount;//首借实际放款金额

    private Integer againLoanCount;//复借人数
    private Double againLoanAmount;//复借金额
    private Double againPayAmount;//复借实际放款金额

    private Double stampTaxAmount;//综合费

    private Date createTime;
    private Date updateTime;
}
