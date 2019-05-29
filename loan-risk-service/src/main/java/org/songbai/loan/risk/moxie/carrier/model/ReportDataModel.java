package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by zengdongping on 17/1/4.
 */

@Data
@Document(collection = "risk_mx_mb_reportdata")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1}",name="idx_user_mobile")
public class ReportDataModel {

    /*

    CREATE TABLE `risk_mx_mb_reportdata` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `task_id` VARCHAR(64) DEFAULT NULL COMMENT '任务id',
  `report_data` longtext NOT NULL COMMENT '报告数据',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
)  COMMENT '手机报告信息';


     */

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;
    private String taskId;
    private String reportData;
    private String message ; // 请求报文
    private String name;
    private String idcard;
    private Date createTime;
    private Date updateTime;
}
