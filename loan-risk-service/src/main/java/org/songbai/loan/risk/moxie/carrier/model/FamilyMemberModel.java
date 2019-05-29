package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 手机号亲情网信息
 */
@Data
@Document(collection = "risk_mx_mb_familynet")
@CompoundIndex(name = "idx_user_mobile",def = "{\"userId\":1,\"mobile\":1,\"familyNetNum\":1}")
public class FamilyMemberModel {

//
    /*


CREATE TABLE `risk_mx_mb_familynet` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `family_net_num` tinyINT(4) NOT NULL COMMENT '当前手机号下所属亲网编号. 用于有多个亲情网的情况, 其值由1开始自增',
  `long_number` VARCHAR(24) NOT NULL COMMENT '成员手机号',
  `short_number` INT(11) NOT NULL COMMENT '成员短号',
  `member_type` VARCHAR(8) NOT NULL COMMENT '成员类型. MASTER-家长; MEMBER-普通成员',
  `join_time` VARCHAR(32) DEFAULT NULL COMMENT '加入日期',
  `expire_time` VARCHAR(32) DEFAULT NULL COMMENT '失效日期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_mobile` (`mobile`),
  PRIMARY KEY (`id`)
) COMMENT '手机号亲情网信息';


     */
    @Id
    private String id;
    @Indexed
    private String mobile;
    @Indexed
    private String userId;
    private String familyNetNum;
    private String longNumber;
    private String shortNumber;
    private String memberType;
    private String joinTime;
    private String expireTime;

    private Date createTime;
    private Date updateTime;
}



