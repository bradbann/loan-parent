/**
 * Project Name:alipay-worker-server
 * File Name:TaobaoUserInfo.java
 * Package Name:com.moxie.cloud.services.alipayworker.dto.taobao
 * Date:2016年7月4日上午11:40:28
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.
 */

package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 淘宝基本信息
 * ClassName: TaobaoUserInfo
 * date: 2016年8月11日 下午9:52:34
 *
 * @author yuandong
 * @since JDK 1.6
 * Modified by liyang on 20171019 接口升级
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TaobaoUserInfo {


    @JsonProperty("mapping_id")
    private String mappingId;
    @JsonProperty("nick")
    private String nick;
    @JsonProperty("real_name")
    private String realName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("vip_level")
    private int vipLevel;
    @JsonProperty("vip_count")
    private int vipCount;
    @JsonProperty("weibo_account")
    private String weiboAccount;
    @JsonProperty("weibo_nick")
    private String weiboNick;
    @JsonProperty("pic")
    private String pic;
    @JsonProperty("alipay_account")
    private String alipayAccount;

    /**
     * 天猫等级（存在为空的情况，客户未开通天猫） T1，T2，T3
     * 说明：20171019接口升级，新增字段
     */
    @JsonProperty("tmall_level")
    private String tmallLevel;

    /**
     * 天猫VIP值（存在为空的情况，客户未开通天猫）
     * 说明：20171019接口升级，新增字段
     */
    @JsonProperty("tmall_vipcount")
    private Integer tmallVipcount;

    /**
     * 天猫信誉 中等、良好、极好 等
     * 说明：20171019接口升级，新增字段
     */
    @JsonProperty("tmall_apass")
    private String tmallApass;

    /**
     * 最早一笔订单交易时间 格式为yyyy-MM-dd HH:mm:ss
     * 说明：20171019接口升级，新增字段
     */
    @JsonProperty("first_ordertime")
    private String firstOrdertime;

    @JsonProperty("taobao_userid")
    private String taobaoUserid;

    @JsonProperty("tao_score")
    private String taoScore;

    @JsonProperty("register_time")
    private Date registerTime;

    @JsonProperty("account_auth")
    private String accountAuth;


    private String gender; // 性别	0-保密；1-男；2-女
    private String birthday; //生日
    private String constellation; //星座
    private String address; //居住地
    private String hometown; //家乡

    @JsonProperty("address_code")
    private String addressCode; //居住地区域编码
    @JsonProperty("hometown_code") // 家乡区域编码
    private String hometownCode;
    @JsonProperty("security_level") // 安全等级
    private String securityLevel;
    private String authentication; // 身份是否认证
    @JsonProperty("login_password")
    private String loginPassword; // 是否设置登录密码

    @JsonProperty("pwd_protect")
    private String pwdProtect; // 是否设置密保问题

    @JsonProperty("phone_bind")
    private String phoneBind; //是否绑定手机号码
}
  
