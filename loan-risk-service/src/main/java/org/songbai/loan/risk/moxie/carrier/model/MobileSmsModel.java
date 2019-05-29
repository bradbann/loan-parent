package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_mx_mb_mobilesms")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1,\"billMonth\":1}",name="idx_user_mobile")
public class MobileSmsModel {

    /*

CREATE TABLE `risk_mx_mb_mobilesms` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `bill_month` VARCHAR(32) DEFAULT NULL COMMENT '账单月',
  `time` VARCHAR(32) DEFAULT NULL COMMENT '收/发短信时间',
  `peer_number` VARCHAR(64) NOT NULL COMMENT '对方号码',
  `location` VARCHAR(64) DEFAULT NULL COMMENT '通信地(自己的)',
  `send_type` VARCHAR(16) DEFAULT NULL COMMENT 'SEND-发送; RECEIVE-收取',
  `msg_type` VARCHAR(8) DEFAULT NULL COMMENT 'SMS-短信; MSS-彩信',
  `service_name` VARCHAR(256) DEFAULT NULL COMMENT '业务名称. e.g. 点对点(网内)',
  `fee` INT(11) DEFAULT NULL COMMENT '通信费(单位分)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_mobile` (`mobile`),
  KEY `idx_user_id` (`user_id`),
  PRIMARY KEY (`id`)
) COMMENT '手机短信详情';


 */

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;
    private String billMonth;
    private String time;
    private String peerNumber;
    private String location;
    private String sendType;
    private String msgType;
    private String serviceName;
    private Integer fee;
    private Date createTime;
    private Date updateTime;


}
  
