package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_tb_deliver_address")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1 }")
public class DeliverAddressModel {

    /*

CREATE TABLE `risk_mx_tb_deliver_address` (
  `id`  BIGINT(32) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255)  DEFAULT '',
  `mapping_id`   VARCHAR(32)  NOT NULL DEFAULT '',
  `name`   VARCHAR(64)   DEFAULT NULL COMMENT '姓名',
  `address`  VARCHAR(128)   DEFAULT NULL COMMENT '地址（到街道）',
  `full_address` VARCHAR(128)   DEFAULT NULL  COMMENT '详细地址',
  `zip_code`  VARCHAR(128)   DEFAULT NULL   COMMENT '邮编',
  `phone_number` VARCHAR(128)   DEFAULT NULL   COMMENT '电话号码',
  `province` VARCHAR(64)   DEFAULT NULL   COMMENT '省',
  `city`   VARCHAR(64)   DEFAULT NULL   COMMENT '市',
  `is_default`   TINYINT(1)  DEFAULT NULL   COMMENT '是否是默认地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tb_user_mappingid` (`user_id`, `mapping_id`) USING BTREE
);

*/

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String mappingId;
    private String name;
    private String address;
    private String fullAddress;
    private String zipCode;
    private String phoneNumber;
    private String province;
    private String city;
    private Integer isDefault;
    private Date createTime;
    private Date updateTime;
}
  
