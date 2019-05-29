package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_tb_trade_detail")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1,\"tradeId\":1 }")
public class TradeDetailModel {


	/*

	CREATE TABLE `risk_mx_tb_trade_detail` (
  `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) DEFAULT '',
  `mapping_id` VARCHAR(32) NOT NULL DEFAULT '',
  `trade_id` VARCHAR(32) DEFAULT NULL COMMENT '交易id',
  `trade_status` VARCHAR(32) DEFAULT NULL COMMENT '交易状态',
  `trade_create_time` DATETIME DEFAULT NULL COMMENT '交易时间',
  `actual_fee` INTEGER(20) DEFAULT '0' COMMENT '实际交易金额',
  `seller_id` BIGINT(20) DEFAULT NULL COMMENT '买家id',
  `seller_nick` VARCHAR(64) DEFAULT NULL COMMENT '卖家nick',
  `seller_shop_name` VARCHAR(64) DEFAULT NULL COMMENT '卖家店铺名称',
  `trade_text` VARCHAR(64) DEFAULT NULL COMMENT '交易状态中文',
  `deliver_name` VARCHAR(128) DEFAULT NULL COMMENT '收货人姓名',
  `deliver_mobile_phone` VARCHAR(32) DEFAULT NULL COMMENT '收货人移动电话',
  `deliver_fixed_phone` VARCHAR(32) DEFAULT NULL COMMENT '收货人固定电话',
  `deliver_address` VARCHAR(128) DEFAULT NULL COMMENT '收货地址',
  `deliver_post_code` VARCHAR(45) DEFAULT NULL COMMENT '邮编',
  `deliver_full_address` VARCHAR(512) DEFAULT NULL COMMENT '收货地址 整体',
  `invoice_name` VARCHAR(128) DEFAULT NULL COMMENT '发票抬头',
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
	private String tradeStatus;
	private Date tradeCreateTime;
	private Integer actualFee;
	private Long sellerId;
	private String sellerNick;
	private String sellerShopName;
	private String tradeText;
	private String deliverName;       //姓名
	private String deliverMobilePhone;//移动电话
	private String deliverFixedPhone; //固定电话
	private String deliverAddress;    //收货地址
	private String deliverPostCode;   //邮编 
	private String deliverFullAddress;//全量地址
	private String invoiceName;// 发票抬头
	private Date createTime;
	private Date updateTime;

}
  
