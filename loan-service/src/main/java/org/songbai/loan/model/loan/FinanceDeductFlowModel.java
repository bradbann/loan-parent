package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_finance_deduct_flow")
public class FinanceDeductFlowModel {
    /*
     CREATE TABLE `loan_a_finance_deduct_flow` (
      `id` int(11) NOT NULL,
      `deduct_id` int(11) NOT NULL COMMENT '自动扣款委托',
      `deduct_level` int(11) NOT NULL COMMENT '自动扣款级别，又是扣款比例*100',
      `user_id` int(11) NOT NULL,
      `agency_id` int(11) NOT NULL,
      `actor_id` int(11) NOT NULL COMMENT '代扣操作人id',
      `actor_name` varchar(128) NOT NULL COMMENT '代扣操作人',
      `order_number` varchar(25) DEFAULT NULL COMMENT '订单号',
      `deduct_number` varchar(25) DEFAULT NULL COMMENT '代扣订单号',
      `name` varchar(25) DEFAULT NULL COMMENT '用户名字',
      `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
      `channel_id` int(11) DEFAULT NULL COMMENT '渠道id',
      `vest_id` int(11) DEFAULT NULL COMMENT '马甲id',
      `payment` double(20,5) DEFAULT NULL COMMENT '应还金额',
      `deduct_money` double(20,5) DEFAULT NULL COMMENT '已经代扣金额',
      `status` tinyint(1) DEFAULT NULL COMMENT '代扣状态 0失败 1成功 2进行中',
      `pay_platform` varchar(25) DEFAULT NULL COMMENT '支付平台名字',
      `bank_name` varchar(25) DEFAULT NULL COMMENT '银行名字',
      `bank_card_num` varchar(25) DEFAULT NULL COMMENT '银行卡号',
      `remark` varchar(255) DEFAULT NULL COMMENT '备注',
      `repayment_date` date DEFAULT NULL COMMENT '应还日期',
      `deduct_time` datetime DEFAULT NULL COMMENT '代扣时间',
      `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;

     */
    private Integer id;

    private Integer deductId;
    private Integer deductLevel;

    private Integer userId;
    private Integer agencyId;

    private String orderNumber;//订单号


    private Integer actorId;//操作人id
    private String actorName; // 提交人


    private String deductNumber; // 代扣订单号
    private Double deductMoney;//本次代扣金额
    private Double alreadyDeduct;//已扣款金额

    private Integer status;//WAIT(1, "等待成功"), SUCCESS(2, "成功"), FAIL(3, "失败");
    private String payPlatform;//付款平台名字

    private String bankName;//银行名字
    private String bankCardNum;//银行卡号

    private String remark;//备注

    private Date repaymentDate;//应还日期
    private Date deductTime;//代扣时间

    private Date createTime;
    private Date updateTime;


}
