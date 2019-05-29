package org.songbai.loan.risk.model.user;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@TableName("risk_user_risk_order")
@Data
public class UserRiskOrderModel {

    /*

        CREATE TABLE `risk_user_risk_order` (
            `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
            `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
            `order_number` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '订单Id',
            `status` INT(11) NOT NULL COMMENT '0 提交订单 ，1 机审完成，2，等待数据',
            `remark` VARCHAR(255) NOT NULL COMMENT '备注',
            `risk_result` INT(11) NOT NULL COMMENT '0:默认; 1:通过; 2:拒绝;3:人工;',
            `risk_result_list` VARCHAR(255) NOT NULL COMMENT '分类风控结果',
            `scoring` INT(11) NOT NULL COMMENT '风控得分',
            `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
            PRIMARY KEY (`id`)
        ) COMMENT '风控订单结果';


     */

    private Integer id;
    private String userId;
    private String orderNumber;
    private Integer mouldId;
    private Integer status; // 0 提交订单 ，1 机审完成，2，等待数据 , 3失败
    private String remark;  // 备注
    private Integer riskResult;
    private String riskResultList;
    private String riskResultMsg; // 风控原因
    private Integer scoring; // 风控得分
    private Date createTime;
    private Date updateTime;
}
