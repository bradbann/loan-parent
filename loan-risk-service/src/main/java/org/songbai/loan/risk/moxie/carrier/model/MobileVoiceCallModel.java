package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_mx_mb_voicecall")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1,\"billMonth\":1}",name="idx_user_mobile")
public class MobileVoiceCallModel {

    /*


    CREATE TABLE `risk_mx_mb_voicecall` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `bill_month` VARCHAR(32) DEFAULT NULL COMMENT '账单月',
  `time` VARCHAR(32) DEFAULT NULL COMMENT '通话时间',
  `peer_number` VARCHAR(32) NOT NULL COMMENT '对方号码',
  `location` VARCHAR(64) DEFAULT NULL COMMENT '通话地(自己的)',
  `location_type` VARCHAR(256) DEFAULT NULL COMMENT '通话地类型. e.g.省内漫游',
  `duration_in_second` INT(11) NOT NULL COMMENT '通话时长(单位秒)',
  `dial_type` VARCHAR(8) DEFAULT NULL COMMENT 'DIAL-主叫; DIALED-被叫',
  `fee` INT(11) DEFAULT NULL COMMENT '通话费(单位分)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_mobile` (`mobile`),
  PRIMARY KEY (`id`)
) COMMENT '手机通话详情';


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
    private String locationType;
    private Integer durationInSecond;
    private String dialType;
    private Integer fee;
    private Date createTime;
    private Date updateTime;


}
  
