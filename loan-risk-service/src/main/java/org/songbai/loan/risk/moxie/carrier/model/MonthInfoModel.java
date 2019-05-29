package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 语音月份信息
 */
@Data
@Document(collection = "risk_mx_mb_monthinfo")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1}",name="idx_user_mobile")
public class MonthInfoModel {

    /*


CREATE TABLE risk_mx_mb_monthinfo (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  month_count INT(11) NOT NULL DEFAULT 0 COMMENT '有通话记录月份数',
  miss_month_count INT(11) NOT NULL DEFAULT 0 COMMENT '通话记录获取失败月份数',
  no_call_month INT(11) NOT NULL DEFAULT 0 COMMENT '无通话记录月份数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
)   COMMENT '语音月份信息';



     */

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;

    private Integer monthCount;

    private Integer missMonthCount;

    private Integer noCallMonth;

    private Date createTime;
    private Date updateTime;

}
