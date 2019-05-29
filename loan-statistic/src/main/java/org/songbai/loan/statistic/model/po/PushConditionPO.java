package org.songbai.loan.statistic.model.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2019/1/14 8:00 PM
 */
@Data
public class PushConditionPO {
    private Integer exceedDays;//逾期天数
    private Double exceedFee;//逾期费用

}
