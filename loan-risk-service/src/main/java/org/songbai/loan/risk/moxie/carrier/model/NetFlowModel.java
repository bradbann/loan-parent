package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_mb_netflow")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1,\"billMonth\":1}",name="idx_user_mobile")
public class NetFlowModel {

    /*

    CREATE TABLE `risk_mx_mb_netflow` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `bill_month` VARCHAR(32) DEFAULT NULL COMMENT '账单月',
  `time` timestamp NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT '上网时间',
  `location` VARCHAR(256) DEFAULT NULL COMMENT '通信地(自己的)',
  `service_name` VARCHAR(256) DEFAULT NULL COMMENT '业务名称.',
  `net_type` VARCHAR(256) DEFAULT NULL COMMENT '网络类型.4g',
  `duration_in_second` INT(11) NOT NULL COMMENT '通信时长(单位秒)',
  `duration_in_flow` INT(11) NOT NULL COMMENT '流量使用量(单位B)',
  `fee` INT(11) NOT NULL COMMENT '通信费(单位分)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
)  COMMENT '手机流量详情';

     */

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;
    private String billMonth;
    private String time;
    private Integer durationInSecond;
    private Integer durationInFlow;
    private String netType;
    private String serviceName;
    private String location;
    private Integer fee;
    private Date createTime;
    private Date updateTime;

}
  
