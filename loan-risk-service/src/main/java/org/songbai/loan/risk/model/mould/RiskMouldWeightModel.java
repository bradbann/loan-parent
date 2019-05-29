package org.songbai.loan.risk.model.mould;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 风控规则模型。
 */
@Data
@TableName("risk_mould_weight")
public class RiskMouldWeightModel{

    /*
                CREATE TABLE `risk_mould_weight` (
          `id` bigint(32) NOT NULL AUTO_INCREMENT,
          `mould_id` int(11) NOT NULL COMMENT 'moxing id ',
          `name` varchar(255) NOT NULL DEFAULT '',
          `catalog` int(11) NOT NULL DEFAULT '0' COMMENT '数据类型  1：基础 2 ,通讯录，3 运营商， 4 ，淘宝，5，魔蝎报告',
          `high_score` int(11) NOT NULL DEFAULT '0' COMMENT '最高分',
          `catalog_count` int(11) NOT NULL DEFAULT '0' COMMENT '分组数量',
          `weight` double(12,4) NOT NULL DEFAULT '1.0000' COMMENT '加权',
          `adopt_score` int(11) NOT NULL DEFAULT '-1' COMMENT '自动通过得分,-1表示不限制',
          `reject_score` int(11) NOT NULL DEFAULT '-1' COMMENT '拒绝得分最高限制,-1表示不限制',
          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          PRIMARY KEY (`id`)
        );

     */


    private Integer id;

    private Integer mouldId;

    private String name;
    private Integer catalog; //  数据类型  1：基础 2 ,通讯录，3 运营商， 4 ，淘宝，5，魔蝎报告
    private Integer highScore;//最高得分
    private Integer lowerScore;//最低得分

    private Integer catalogCount; // 分组数量。

    private Double weight; // 加权

    private Integer adoptScore; // 自动通过得分 。
    private Integer rejectScore; //拒绝得分最高限制。


    private Date createTime;
    private Date updateTime;

}
