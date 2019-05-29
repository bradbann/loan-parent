package org.songbai.loan.model.version;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@TableName("dream_u_app_vest")
@Data
public class AppVestModel {

    private Integer id;
    private Integer agencyId;
    private Integer groupId;//标的分组id
    private Integer pushSenderId;//推送通道id
    private String pactId;//用户协议id
    private String name;//马甲名称
    private String vestCode;//马甲随机code
    private Integer refuseStatus;//审核拒绝状态,0-禁用，1-启用
    private String refuseJumpUrl;//审核拒绝后的跳转链接
    private Integer status;//马甲是否启用,0-否,1-是
    private Integer vestType;//是否默认,0-否,1-是
    private Date createTime;
    private Integer platform;//1-ios,2-android,3-全部

}
