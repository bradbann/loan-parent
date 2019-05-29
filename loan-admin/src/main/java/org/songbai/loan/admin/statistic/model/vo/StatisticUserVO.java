package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class StatisticUserVO {
    private Integer agencyId;
    private String agencyName;
    private Integer vestId;
    private String vestName;

    private LocalDate statisticDate;//统计日期/用户的注册日期

    private Integer registerCount;//注册人数
    //private Integer payCount;//放款数量
    private Integer loginCount;//登录人数
    private Integer idcardCount;//实名认证人数
    private Integer faceCount;//身份识别数量
    private Integer infoCount;//个人信息数量
    private Integer phoneCount;//运营商认证数量
    private Integer aliCount;//淘宝认证数量
    private Integer bankCount;//绑卡数量

    private Integer orderCount;//总提单人数
    private Integer newCount;//新客提单人数
    private Integer oldCount;//老客提单人数
}
