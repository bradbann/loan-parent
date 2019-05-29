package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_tb_userinfo")
@CompoundIndex(name = "idx_user_mapppingid",def = "{ \"userId\":1, \"mappingId\": 1 }")
public class UserInfoModel {



	/*
CREATE TABLE `risk_mx_tb_userinfo` (
  `id` BIGINT(32) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) DEFAULT '',
  `mapping_id` VARCHAR(64) DEFAULT '',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `nick` VARCHAR(64) DEFAULT '' COMMENT '淘宝昵称',
  `phone_number` VARCHAR(32) DEFAULT NULL COMMENT '电话号码（中间四位为*）',
  `email` VARCHAR(32) DEFAULT NULL COMMENT '邮箱（中间带*）',
  `vip_level` INT(20) DEFAULT NULL COMMENT 'vip等级',
  `vip_count` INT(20) DEFAULT NULL COMMENT 'vip值(约等于购物金额)',
  `weibo_account` VARCHAR(64) DEFAULT NULL COMMENT '绑定的微博账号',
  `weibo_nick` VARCHAR(64) DEFAULT NULL COMMENT '绑定的微博昵称',
  `pic` VARCHAR(512) DEFAULT NULL COMMENT '头像图片',
  `alipay_account` VARCHAR(64) DEFAULT NULL COMMENT '关联支付宝账号',
  `tmall_level` VARCHAR(192) DEFAULT NULL COMMENT '天猫等级',
  `tmall_vipcount` INT(20) DEFAULT NULL COMMENT '天猫VIP值',
  `tmall_apass` VARCHAR(192) DEFAULT NULL COMMENT '天猫信誉',
  `first_ordertime` VARCHAR(192) DEFAULT NULL COMMENT '最早一笔订单交易时间',
  taobao_userid VARCHAR(255) COMMENT '用户在淘宝中的用户ID',
  tao_score VARCHAR(64) COMMENT '淘气值',
  register_time DATETIME DEFAULT NULL COMMENT '注册时间',
  account_auth VARCHAR(64) COMMENT '是否认证',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mapping_id` (`user_id`, `mapping_id`)
) ;



	 */


	@Id
	private String id;
	@Indexed
	private String userId;
	@Indexed
	private String mappingId;
	private String nick;
	private String realName;
	private String phoneNumber;
	private String email;
	private Integer vipLevel;
	private Integer  vipCount;
	private String weiboAccount;
	private String weiboNick;
	private String pic;
	private String alipayAccount;
    private Date createTime;
    private Date updateTime;

	/**
	 * 天猫等级（存在为空的情况，客户未开通天猫） T1，T2，T3
	 * 说明：20171019接口升级，新增字段
	 */
	private String tmallLevel;

	/**
	 * 天猫VIP值（存在为空的情况，客户未开通天猫）
	 * 说明：20171019接口升级，新增字段
	 */
	private Integer tmallVipcount;

	/**
	 * 天猫信誉 中等、良好、极好 等
	 * 说明：20171019接口升级，新增字段
	 */
	private String tmallApass;

	/**
	 * 最早一笔订单交易时间 格式为yyyy-MM-dd HH:mm:ss
	 * 说明：20171019接口升级，新增字段
	 */
	private String firstOrdertime;

	private String taobaoUserid;

	private String taoScore;

	private Date registerTime;

	private String accountAuth;

}
  
