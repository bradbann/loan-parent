package org.songbai.loan.model.agency;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ex_exchange_admin")
public class ExchangeAdminModel implements Serializable {
    private Integer id;
    /**
     * 管理员id
     */
    private Integer actorId;
    private Integer userId;
    private String agencyRelationCode;
    /**
     * 1 平台统一；2 自定义
     */
    private Integer brandType;
    /**
     * 交易所名称:{"zh_CN":"简体中文名称","zh_HK":"繁体中文名字","en":"english name")
     */
    private String exchangeName;
    /**
     * 公司名称:{"zh_CN":"简体中文名称","zh_HK":"繁体中文名字","en":"english name")
     */
    private String companyName;
    /**
     * 商务邮箱
     */
    private String businessEmail;
    /**
     * 交易所logo
     */
    private String logo;
    /**
     * 0 不使用独立域名，1 独立域名(解析生效)
     */
//    private Integer domainType;
    /**
     * pc域名
     */
//    private String pcDomain;
    /**
     * h5域名
     */
//    private String h5Domain;
    /**
     * 内容管理1 平台统一, 2 自行维护
     */
    private Integer contentType;
    /**
     * 是否启用当前自定义 0 不启用，1 启用
     */
    private Integer status;

    /**
     * 邮箱地址
     */
    private String emailAddress;

    /**
     *  发送服务器
     */
    private String emailHost;

    /**
     * 发送服务器端口号
     */
    private Integer emailPort;

    /**
     * 是否认证，0-否，1-是
     */
    private Integer emailAuth;
    /**
     * 邮箱用户名
     */
    private String emailUserName;
    /**
     * 邮箱密码
     */
    private String emailPassWord;
    /**
     * 代理id
     */
    private Integer agencyId;
    /**
     * 更新时间
     */
    private Date updateTime;
}
