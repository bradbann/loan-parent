package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_mb_monthitem")
public class MonthItemModel {

    /*

CREATE TABLE risk_mx_mb_monthitem (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  month VARCHAR(6) NOT NULL DEFAULT '' COMMENT '月份yyyyMM',
  value INT(11) NOT NULL DEFAULT 0 COMMENT '采集结果',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
)  COMMENT '通话记录月份采集结果';

     */
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;

    private String month;
    private Integer value;

    private Date createTime;
    private Date updateTime;
}
