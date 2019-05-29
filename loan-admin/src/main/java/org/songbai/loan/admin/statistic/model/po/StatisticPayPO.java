package org.songbai.loan.admin.statistic.model.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class StatisticPayPO {
    private Integer agencyId;
    private String startDate;//开始时间
    private String endDate;//结束时间
    private Integer isVest = 0;//马甲统计
    private Integer vestId;//马甲id
    private Integer summary = 0;//是否汇总
}
