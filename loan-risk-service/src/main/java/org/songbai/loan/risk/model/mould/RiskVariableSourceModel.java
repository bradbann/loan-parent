package org.songbai.loan.risk.model.mould;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 变量来源
 * 可以有多个来源的变量， 合并后处理。
 */
@Data
@TableName("risk_variable_source")
public class RiskVariableSourceModel {

    /*
    CREATE TABLE `risk_variable_source` (
      `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
      `sources` VARCHAR(64) NOT NULL COMMENT '数据来源',
      `variable_code` VARCHAR(64) NOT NULL COMMENT '变量 代码',
      `catalog` int(11) NOT NULL COMMENT '数据类型  1：基础 2 ,通讯录，3 运营商， 4 ，淘宝，5，魔蝎报告',
      `variable` VARCHAR(255) COMMENT  '数据变量' ,
      `variable_type` int(11) NOT NULL default '0' COMMENT '// 表达式类型 ，0 ，默认， 1 map提取，2 aviator 提取' ,
      `status` int(1) NOT NULL default '0' COMMENT '状态,1启用，0：禁用',
      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ;

     */


    private Long id;

    private String sources;// 数据来源，

//    private Integer variableId;
    private String variableCode;
    private Integer catalog;

//    private String variableFrom; // 数据变量来源 risk_mx_tb_reportdata 表示表
    private String variable; // 数据变量。 需要从json 里面提取数据。
    private Integer variableType;// 表达式类型 ，0 ，默认， 1 map提取，2 aviator 提取 ，

    private Integer status;

    private Date createTime;
    private Date updateTime;

}
