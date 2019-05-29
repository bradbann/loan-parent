package org.songbai.loan.risk.model.user;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 风控用户关联信息表
 * 需要注意这个表中知会有一个记录。
 * 主要的目的是
 */
@Data
@TableName("risk_user_data_task")
public class RiskUserDataTaskModel {

    private Integer id;
    private String userId;// 用户ID ;
    private String sources;// 数据来源，
    private String taskId; // 第三方唯一表示
    private String account; // 账户
    private Integer status; // 1, 提交事务成功， 2，提交失败，3 ，授权成功，4授权失败， 5，获取数据成功。
    private String remark;
    private Date createTime;
    private Date updateTime;


    /*
    	CREATE TABLE `risk_user_data_task` (
          `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
          `user_id` VARCHAR(255) DEFAULT '',
          `sources` VARCHAR(64) NOT NULL COMMENT '数据来源',
          `task_id` VARCHAR(128) DEFAULT NULL COMMENT '第三方唯一表示',
          `account` VARCHAR(128) DEFAULT NULL COMMENT '第三方账户',
          `status` int(11) NOT NULL COMMENT '事务状态，0:失败， 1成功',
          `remark` VARCHAR(320)  COMMENT '备注',
          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          PRIMARY KEY (`id`)
        ) ;
     */
}
