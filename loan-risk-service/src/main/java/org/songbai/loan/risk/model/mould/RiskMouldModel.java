package org.songbai.loan.risk.model.mould;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 风控规则模型。
 */
@Data
@TableName("risk_mould")
public class RiskMouldModel {

    /*

           CREATE TABLE `risk_mould_variable` (
          `id` int(11) NOT NULL AUTO_INCREMENT,
          `mould_id` int(11) NOT NULL COMMENT 'moxing id ',
          `catalog` int(11) NOT NULL COMMENT '数据类型  1：基础，2 运营商， 3 ，淘宝',
          `variable_code` varchar(255) NOT NULL COMMENT '变量code',
          `variable_name` varchar(255) NOT NULL,
          `calc_symbol` varchar(255) NOT NULL DEFAULT '=' COMMENT '计算符号',
          `calc_left` varchar(255) DEFAULT NULL COMMENT '计算符号左侧',
          `calc_right` varchar(255) DEFAULT NULL COMMENT '计算符号右侧',
          `oper_type` int(11) NOT NULL DEFAULT '2' COMMENT '1:赋得分，  2:拒绝，3：转人工',
          `oper_score` int(11) NOT NULL DEFAULT '0' COMMENT '得分赋值',
          `risk_level` int(11) NOT NULL DEFAULT '0' COMMENT '风险等级 0 无 1,2,3',
          `status` int(11) DEFAULT '0' COMMENT '0禁用1启用',
          `remark` varchar(255) DEFAULT NULL,
          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          PRIMARY KEY (`id`)
        );


     */

    private Integer id;
    private String name;
    private Integer status;
    private Integer defaultSore; //默认得分 。
    private Integer scoreType ; // 得分计算模式， -1 负分模式，0，默认模式， 1: 正分模式

    private Integer riskMaxScore; //  风险项 最高得分 操作最高得分自动拒绝 12
    private Integer lowerRiskScore; //  风险项 低风险得分 0 ，
    private Integer middleRiskScore; //  风险项 中风险得分 3 ，
    private Integer highRiskScore; // 风险项 高风险得分 6


    private Integer adoptScore; // 自动通过得分 。
    private Integer rejectScore; //拒绝得分最高限制。


    private Double femaleWeight; // 女性加权
    private Double maleWeight;  // 男性加权

    private Date createTime;
    private Date updateTime;

}
