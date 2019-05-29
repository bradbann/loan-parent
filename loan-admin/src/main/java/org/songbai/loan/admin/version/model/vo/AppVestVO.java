package org.songbai.loan.admin.version.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2019/1/8 4:43 PM
 */
@Data
public class AppVestVO {
    private Integer id;
    private Integer groupId;
    private String name;//马甲名称
    String vestCode;//马甲随机code
    Integer refuseStatus;//审核拒绝状态,0-禁用，1-启用
    String refuseJumpUrl;//审核拒绝后的跳转链接
    String pactId;//用户协议id
    String pactName;//用户协议名称
    Integer status;//马甲是否启用,0-否,1-是
    Integer vestType;//是否默认,0-否,1-是
    private Integer agencyId;
    private String agencyName;
    private Date createTime;
    private String groupName;
    private Integer platform;//来源
    private String pushSender;//推送名称
    private String pushSenderId;//推送id
}
