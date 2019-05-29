package org.songbai.loan.risk.model.mould;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 记录风控变量
 */
@Data
@TableName("risk_variable")
public class RiskVariableModel {

        /*


        CREATE TABLE `risk_variable` (
          `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
          `name` VARCHAR(255) not null default '' ,
          `catalog` int(11) NOT NULL default '0' COMMENT '数据类型  1：基础 2 ,通讯录，3 运营商， 4 ，淘宝，5，魔蝎报告',
          `code` VARCHAR(255) not null default '' ,
          `remark` VARCHAR(255) not null default '' ,
          `status` int(11) NOT NULL default '0' COMMENT '状态,1启用，0：禁用',
          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          PRIMARY KEY (`id`)
        ) ;

     */


    private Long id;


    private Integer catalog; //  数据类型  1：基础报告，2 运营商， 3 ，淘宝

    private String code;// 数据编码
    private String name;// 变量名
    private String remark; // 变量备注

    private Integer status;

    private Date createTime;
    private Date updateTime;

}
