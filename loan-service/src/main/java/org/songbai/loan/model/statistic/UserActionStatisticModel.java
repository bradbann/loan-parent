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
@TableName("loan_s_user")
public class UserActionStatisticModel {
    private Integer id;
    private Integer agencyId;
    private Integer vestId;
    private String channelCode;

    private LocalDate actionDate;//统计日期/用户的注册日期
    private Integer uvCount;//uv
    private Integer registerCount;//注册人数
    private Integer payCount;//放款数量
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

    private Date createTime;
    private Date updateTime;
}
