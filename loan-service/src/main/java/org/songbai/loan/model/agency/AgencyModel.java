package org.songbai.loan.model.agency;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("dream_u_agency")
public class AgencyModel implements Serializable {
    private Integer id;
    private String agencyName;//代理名
    private String agencyCode;//代理登陆码
    private String relationCode;//关联code
    private Integer superId;// 上级id
    private String linkMan;//联系人
    private String linkPhone;// 联系电话
    private String description;//描述
    private String idMd5;//主键加密
    private Integer mouldId;//新客风控模型id
    private Integer oldGuestMouldId;//老客客风控模型id

    @TableField(exist = false)
    private String agencyUrl;//代理专属url

    private Integer userId;//代理对应的userId
    private Integer agencyLevel;//代理级别
    private String agencyIcon;//代理icon
    private Integer status;//代理状态 默认1
    private Integer deleted; //删除
    private Date createTime;
    private Date updateTime;

    private Integer jumpLoan;//是否跳转到贷超,0-否，1-是
    private String jumpUrl;//跳转链接
    private Integer alipayStatus;//支付宝,0-否，1-是
    private String alipayUrl;//跳转链接
    private Integer wepayStatus;//微信支付开关 1开 0 关
    private String wepayUrl;//微信地址跳转链接
    private Integer h5Status;//H5支付开关 1开 0关
    private String h5Url;//h5地址
    private Integer autoPay;//自动放款 0 否, 1 是
    private Integer badDebt;//自动坏账天数 默认30
    private String jhpayMerid;//聚合支付账号
    private String jhpayKey;//聚合支付密码

}