package org.songbai.loan.model.statistic.dto;


import lombok.Data;

import java.time.LocalDate;

/**
 * Author: qmw
 * Date: 2018/11/24 3:19 PM
 */
@Data
public class UserStatisticDTO {

    private Integer agencyId;
    private String channelCode;
    private Integer vestId;

    private LocalDate registerDate;//用户的注册日期
    private LocalDate actionDate;//行为时间

    private Integer isRegister = 0;//是否注册 0 否
    private Integer isActionLogin = 0;//是否用户行为登录
    private Integer isLogin = 0;//是否登录 0否

    private Integer isIdcard = 0;//是否实名认证
    private Integer isPhone = 0;//是否运营商认证
    private Integer isFace = 0;//是否身份识别
    private Integer isInfo = 0;//是否个人信息
    private Integer isAli = 0;//是否淘宝认证
    private Integer isBank = 0;//是否绑卡
    private Integer isPay = 0;//是否放款

    private Integer isNew = 0;//是否新客提单
    private Integer isOld = 0;//是否老客提单人数
    private Integer isUv = 0;//渠道uv统计
}
