package org.songbai.loan.admin.push.model.vo;

import lombok.Data;

import java.sql.Date;

/**
 * Author: qmw
 * Date: 2019/1/10 4:10 PM
 */
@Data
public class PushSenderVO {
    private Integer id;
    private Integer agencyId;
    private String agencyName;
    private String name;
    private Integer type; //推送平台 1.个推
    private String appId;
    private String appKey;
    private String master;
    private String url;//个推使用
    private Integer status; //0停用 1启用
    private Date createTime;
    private Date updateTime;
}
