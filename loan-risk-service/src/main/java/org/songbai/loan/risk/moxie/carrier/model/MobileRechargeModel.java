package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_mb_recharge")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1}",name="idx_user_mobile")
public class MobileRechargeModel {


    /*

    CREATE TABLE `risk_mx_mb_recharge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `recharge_time` VARCHAR(32) DEFAULT NULL COMMENT '充值时间',
  `amount` INT(11) NOT NULL COMMENT '充值金额(单位分)',
  `type` VARCHAR(256) DEFAULT NULL COMMENT '充值方式. e.g. 现金',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_mobile` (`mobile`),
  PRIMARY KEY (`id`)
) COMMENT '手机充值信息';

     */

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed private String mobile;
    //	private String billMonth;
    private String rechargeTime;
    private Integer amount;
    private String type;
    private Date createTime;
    private Date updateTime;


}
  
