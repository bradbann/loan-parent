package org.songbai.loan.model.sms;

import lombok.Data;

import java.util.Set;

@Data
public class PushModel {

    private Integer id;
    //private Integer agencyId;
    private Integer vestId;//马甲id

    private Integer classify;// 系统消息

    private String msg;//内容

    private String title;//标题

    private String url;//跳转的地址

    private Integer isJump = 0;//是否跳转 1跳转

    private Integer type;//类型
    private Integer subType;//子类型

    private String dataId;//数据id

    private Integer userId;

    private String deviceId;//设备id

    private Set<String> deviceIds;//要推送的用户组群
}

