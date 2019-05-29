package org.songbai.loan.admin.statistic.model.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class StatisticRepayPO {
    private Integer agencyId;
    private String startDate;//开始时间
    private String endDate;//结束时间
}
