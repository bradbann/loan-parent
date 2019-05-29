package org.songbai.loan.risk.model.mould;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 风控模型变量。
 */
@Data
@TableName("risk_mould_variable")
public class RiskMouldVariableModel {

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
          PRIMARY KEY (`id`);
        )
     */


    private Integer id;

    private Integer mouldId;
    private Integer catalog; //  数据类型  1：基础，2 运营商， 3 ，淘宝
    private String variableCode; // 变量code
    private String variableName;//标签名称

    private String calcSymbol; // 计算符号 ,
    private String calcLeft;// 计算符号左侧
    private String calcRight;// 计算符号右侧


    private Integer operType; // 1:赋得分，  2:拒绝，3：转人工 。
    private Integer operScore; // 得分赋值

    /**
     * @link {VariableConst.RISK_LEVEL_*}
     */
    private Integer riskLevel;//  风险等级 0 无 1,2,3

    private Integer status;
    private String remark;
    private Integer indexed; // 排序字段。


    private Date createTime;
    private Date updateTime;
}
