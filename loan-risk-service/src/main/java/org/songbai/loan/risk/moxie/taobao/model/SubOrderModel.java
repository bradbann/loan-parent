package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_tb_suborder")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1,\"tradeId\":1 }")
public class SubOrderModel {

    /*

CREATE TABLE `risk_mx_tb_suborder` (
  `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) DEFAULT '',
  `mapping_id` VARCHAR(32) NOT NULL DEFAULT '',
  `trade_id` VARCHAR(32) DEFAULT NULL COMMENT '交易id',
  `item_id` VARCHAR(32) DEFAULT NULL COMMENT '商品id',
  `item_url` VARCHAR(128) DEFAULT NULL COMMENT '商品url',
  `item_pic` VARCHAR(256) DEFAULT NULL COMMENT '商品图片',
  `item_name` VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
  `original` INTEGER(20) DEFAULT '0' COMMENT '原始商品价格',
  `real_total` INTEGER(20) DEFAULT '0' COMMENT '真实交易价格',
  `quantity` INT(11) DEFAULT NULL COMMENT '商品数量',
  cid_level1 VARCHAR(64) DEFAULT NULL COMMENT '一级目录的id',
  cid_level2 VARCHAR(64) DEFAULT NULL COMMENT '二级目录的id',
  cname_level1 VARCHAR(255) DEFAULT NULL COMMENT '一级目录的名称',
  cname_level2 VARCHAR(255) DEFAULT NULL COMMENT '二级目录的名称',
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
    private String itemId;
    private String itemUrl;
    private String itemPic;
    private String itemName;
    private Integer original;
    private Integer realTotal;
    private Integer quantity;
    private Date createTime;
    private Date updateTime;

    private String cidLevel1;

    private String cidLevel2;

    private String cnameLevel1;

    private String cnameLevel2;

}
  
