package org.songbai.loan.risk.moxie.carrier.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_mx_mb_mobilebill")
@CompoundIndex(def = "{\"userId\":1,\"mobile\":1,\"billMonth\":1}",name="idx_user_mobile")
public class MobileBillModel {


    /*


CREATE TABLE `risk_mx_mb_mobilebill` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
  `mobile` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '手机号码',
  `bill_month` VARCHAR(32) DEFAULT NULL COMMENT '账单月',
  `bill_start_date` VARCHAR(32) COMMENT '账单日起始日yyyy-MM-01',
  `bill_end_date` VARCHAR(32) COMMENT '账单日结束日yyyy-MM-01',
  `base_fee` INT(11) DEFAULT NULL COMMENT '套餐及固定费(单位分)',
  `extra_service_fee` INT(11) DEFAULT NULL COMMENT '增值业务费(单位分)',
  `voice_fee` INT(11) DEFAULT NULL COMMENT '语音费(单位分)',
  `sms_fee` INT(11) DEFAULT NULL COMMENT '短彩信费(单位分)',
  `web_fee` INT(11) DEFAULT NULL COMMENT '网络流量费(单位分)',
  `extra_fee` INT(11) DEFAULT NULL COMMENT '其它费用(单位分)',
  `total_fee` INT(11) DEFAULT NULL COMMENT '总费用(单位分)',
  `discount` INT(11) DEFAULT NULL COMMENT '优惠费(单位分)',
  `extra_discount` INT(11) DEFAULT NULL COMMENT '其它优惠(单位分). 包括他人代付, 第三方支付等',
  `actual_fee` INT(11) DEFAULT NULL COMMENT '个人实际费用(单位分)',
  `paid_fee` INT(11) DEFAULT NULL COMMENT '已支付费用(单位分)',
  `unpaid_fee` INT(11) DEFAULT NULL COMMENT '未支付费用(单位分)',
  `related_mobiles` text COMMENT '关联的手机号',
  `point` INT(11) DEFAULT NULL COMMENT '当前可用积分',
  `last_point` INT(11) DEFAULT NULL COMMENT '上期可用积分',
  `notes` VARCHAR(1024) DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) COMMENT '手机账单信息';

     */
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mobile;
    private String billMonth;
    private String billStartDate;
    private String billEndDate;
    private Integer baseFee;
    private Integer extraServiceFee;
    private Integer voiceFee;
    private Integer smsFee;
    private Integer webFee;
    private Integer extraFee;
    private Integer totalFee;
    private Integer discount;
    private Integer extraDiscount;
    private Integer actualFee;
    private Integer paidFee;
    private Integer unpaidFee;
    private Integer point;
    private Integer lastPoint;
    private String relatedMobiles;
    private String notes;
    private Date createTime;
    private Date updateTime;
}
  
