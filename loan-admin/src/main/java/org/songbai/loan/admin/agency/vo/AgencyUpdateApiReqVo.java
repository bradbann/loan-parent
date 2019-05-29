package org.songbai.loan.admin.agency.vo;

import lombok.Data;

@Data
public class AgencyUpdateApiReqVo {
    private Integer id;
    private Integer apiPermission;//api是否开放，0 关闭。1 开放 2 按用户分组
    private Integer otcPermission;//法币交易是否开放.0 关闭，1 开放
    private Integer invitePermission;//邀请是否开放.0 关闭，1 开放，
    private Integer pcPermission;//PC下载入口是否开放.0 关闭，1 开放，
    private String iosUrl;//iOS下载地址
    private String androidUrl;//安卓下载地址
}
