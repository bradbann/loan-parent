package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/21 4:38 PM
 */
@Data
public class StatisHomeVO {
    private String date;//统计日期/用户的注册日期
    private Integer registerCount = 0;//注册人数
    private Integer loginCount = 0;//登录人数

    private Integer idcardCount = 0;//实名认证人数
    private Integer faceCount = 0;//身份识别数量
    private Integer infoCount = 0;//个人信息数量
    private Integer phoneCount = 0;//运营商认证数量
    private Integer aliCount = 0;//淘宝认证数量
    private Integer bankCount = 0;//绑卡数量

    private Integer orderCount = 0;//总提单人数
    private Integer newCount = 0;//新客提单人数
    private Integer oldCount = 0;//老客提单人数

    private Integer payCount = 0;//放款笔数
    private String payAmount;//放款金额

    private Integer firstLoanCount = 0;//新客下款量

    private Integer overdueCount = 0;//逾期中数量

}
