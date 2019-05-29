package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_mb_mobilebasic")
@CompoundIndex(name = "idx_user_mobile",def = "{\"userId\":1,\"mobile\":1 }")
public class MobileBasicModel {

    /*


    CREATE TABLE `risk_mx_mb_mobilebasic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `name` varchar(24) DEFAULT '' COMMENT '姓名',
  `mobile` varchar(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `idcard` varchar(64) DEFAULT '' COMMENT '证件号',
  `carrier` varchar(16) NOT NULL COMMENT '1-移动; 2-联通; 3-电信',
  `province` varchar(24) NOT NULL COMMENT '省份',
  `city` varchar(8) DEFAULT NULL COMMENT '城市',
  `open_time` date DEFAULT NULL COMMENT '开卡时间',
  `level` varchar(8) DEFAULT NULL COMMENT '星级',
  `package_name` varchar(128) DEFAULT NULL COMMENT '手机套餐名称',
  `available_balance` int(11) DEFAULT NULL COMMENT '可用余额(单位分)',
  `state` int(11) DEFAULT NULL,
  `reliability` int(2) DEFAULT '0' COMMENT '实名状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手机基本信息';

     */
    @Id
    private String id;
    @Indexed
    private String mobile;
    @Indexed
    private String userId;
    private String name;
    private String idCard;
    private String carrier;
    private String province;
    private String city;
    private Date openTime;
    private String level;
    private String packageName;
    private Integer state;
    private Integer availableBalance;
    private Date createTime;
    private Date updateTime;
    private Integer reliability;

}
  
