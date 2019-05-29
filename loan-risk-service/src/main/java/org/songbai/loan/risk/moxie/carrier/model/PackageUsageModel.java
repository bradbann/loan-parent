package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_mb_packageusage")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1}",name="idx_user_mobile")
public class PackageUsageModel {

    /*

CREATE TABLE `risk_mx_mb_packageusage` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `bill_start_date` DATE NOT NULL COMMENT '账单起始日',
  `bill_end_date` DATE NOT NULL COMMENT '账单结束日',
  `item` VARCHAR(512) NOT NULL COMMENT '项目名称. e.g.手机上网自选30元包.流量单位l默认KB, 语音默认秒, 短信默认条',
  `unit` VARCHAR(64) NOT NULL COMMENT '单位',
  `total` VARCHAR(16) NOT NULL COMMENT '总量',
  `used` VARCHAR(16) NOT NULL COMMENT '已使用量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
) COMMENT '手机套餐项目使用信息';

     */
    @Id
    private String id;
    private String billStartDate;
    private String billEndDate;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;
    private String item;//项目名称. e.g.手机上网自选30元包(500MB)
    private String total;
    private String used;
    private String unit;
    private Date createTime;
    private Date updateTime;
}
  
