/**  
 * Project Name:alipay-worker-server  
 * File Name:DeliverAddress.java  
 * Package Name:com.moxie.cloud.services.alipayworker.model.taobao
 * Date:2016年6月13日下午4:47:34  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_mx_tb_recent_address")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1,\"tradeId\":1 }")
public class RecentAddressModel {

	/*

	CREATE TABLE `risk_mx_tb_recent_address` (
  `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) DEFAULT '',
  `mapping_id` VARCHAR(32) NOT NULL DEFAULT '',
  `trade_id` VARCHAR(128) DEFAULT NULL COMMENT '交易编号',
  `trade_create_time` DATETIME DEFAULT NULL COMMENT '交易时间',
  `deliver_name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `deliver_mobile_phone` VARCHAR(64) DEFAULT NULL COMMENT '移动电话',
  `deliver_fixed_phone` VARCHAR(64) DEFAULT NULL COMMENT '固定电话',
  `deliver_address` VARCHAR(128) DEFAULT NULL COMMENT '收货地址',
  `deliver_post_code` VARCHAR(64) DEFAULT NULL COMMENT '邮编',
  `invoice_name` VARCHAR(128) DEFAULT NULL COMMENT '发票抬头',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `actual_fee` INTEGER(20) COMMENT '交易金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mapping_id` USING BTREE (`user_id`, `mapping_id`, `trade_id`)
) ;


	 */
	@Id
	private String id;
	@Indexed
	private String userId;
	@Indexed
	private String mappingId;
	private String tradeId;
	private Date tradeCreateTime;
	private Integer actualFee;
	private String deliverName;       //姓名
	private String deliverMobilePhone;//移动电话
	private String deliverFixedPhone; //固定电话
	private String deliverAddress;    //收货地址
	private String deliverPostCode;   //邮编 
	private String invoiceName;// 发票抬头
	private String province;
	private String city;
	private Date createTime;
	private Date updateTime;
}
  
