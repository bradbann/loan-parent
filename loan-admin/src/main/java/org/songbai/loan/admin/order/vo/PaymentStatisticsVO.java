package org.songbai.loan.admin.order.vo;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/13 11:30 AM
 */
@Data
public class PaymentStatisticsVO {
    private Integer payCountDay;//放款人数（个)
    private String payMoneyDay;//今日已放款（元）
    private Integer payCountWeek;//放款人数（周)
    private String payMoneyWeek;//今日已放款（元）
    private Integer payCountAll;//放款人数（总)
    private String payMoneyAll;//已放款（元）
}
