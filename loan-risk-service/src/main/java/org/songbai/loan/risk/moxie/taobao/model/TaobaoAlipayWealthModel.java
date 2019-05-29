package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_mx_tb_alipay_wealth")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1 }")
public class TaobaoAlipayWealthModel {



    /*


CREATE TABLE `risk_mx_tb_alipay_wealth` (
  `id`                      BIGINT(32)       NOT NULL       AUTO_INCREMENT,
  `user_id`                  VARCHAR(255) DEFAULT '',
  `mapping_id`               VARCHAR(32) NOT NULL       DEFAULT '',
  `balance`                 INTEGER(20)    DEFAULT NULL COMMENT '账户余额',
  `total_profit`             INTEGER(20)    DEFAULT NULL COMMENT '余额宝历史累计收益',
  `total_quotient`           INTEGER(20) DEFAULT NULL  COMMENT '余额宝金额',
  `hua_bei_credit_amount`      INTEGER(20)    DEFAULT NULL  COMMENT '花呗当前可用额度',
  `hua_bei_total_credit_amount` INTEGER(20)    DEFAULT NULL  COMMENT '花呗授信额度',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mapping_id` (`user_id`, `mapping_id`) USING BTREE
);



     */

    @Id
    private String id;
    @Indexed
    private String userId;
    private Date createTime;
    private Date updateTime;
    @Indexed
    private String mappingId;

    private Integer balance;

    private Integer totalProfit;

    private Integer totalQuotient;

    private Integer huabeiCreditamount;

    private Integer huabeiTotalcreditamount;

}
