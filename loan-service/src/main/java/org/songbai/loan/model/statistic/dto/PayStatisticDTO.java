package org.songbai.loan.model.statistic.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/24 3:19 PM
 * 放款统计
 */
@Data
public class PayStatisticDTO {

    private Integer agencyId;
    private Integer vestId;//马甲id

    private LocalDate payDate;//放款日期

    private LocalDate repayDate;//还款日期

    private Double loan = 0D;//借款金额

    private Double pay = 0D;//实际放款金额

    private Integer isFirstLoan = 0;//是否首借 0 否

    private Integer isAgainLoan = 0;//是否复借 0 否

    private Double stampTax = 0D;//综合费
}
