package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户类
 *
 * @author wjl
 * @date 2018年10月29日 13:42:06
 * @description
 */
@Data
@TableName("loan_u_user")
public class UserModel implements Serializable {

    @TableId
    private Integer id;
    private String thirdId;//调用第三方使用的userId
    private String name;//姓名
    private String phone;//手机号码
    private String userPass;//登陆密码
    private String passSalt;//加密言（H，D）
    private Integer passEncryptTimes;//密码加密次数（D）
    private Integer loginErrorNum;//密码登陆输入错误次数
    private Date lastLoginTime;//最后登陆时间

    private Integer agencyId;

    private Integer status; //用户状态 0黑名单,1正常 2灰名单 3白名单
    private String deviceId;//设备号

    private String gexing;//个推id
    private String mobileName;//手机品牌
    private String mobileType;//手机型号
    private String systemVersion;//系统版本
    private String appVersion;//应用版本

    private String platform;//平台（ios、安卓、h5）
    private String channelCode;//渠道来源（小米商店、App Store）
    private String registrationIp;//注册ip
    private String loginIp;//登录ip
    private Integer loginNum;//登录次数

    private String remark;
    private Integer guest;//客群 1新客 2次新 3老客
    private Integer deleted;//0未删除 1已删除
    private Integer channelId;
    private Integer vestId;
    private String vestCode;

    private Date createTime;
    private Date updateTime;
}
