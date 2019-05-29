-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库

-- user表修改
ALTER TABLE `loan_u_user`
  DROP COLUMN `market_id`,
  MODIFY COLUMN `market` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道来源（小米商店、App Store）' AFTER `channel_id`,
  ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '渠道id' AFTER `channel_id`,
  ADD COLUMN `vest_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '马甲随机数' AFTER `vest_id`,
  CHANGE COLUMN `market` `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道来源（小米商店、App Store）' AFTER `vest_code`;

-- 短信表修改
ALTER TABLE  `dream_m_sms_sender`
  DROP COLUMN `category`,
  DROP COLUMN `super_id`;
-- 短信模板修改
ALTER TABLE `dream_m_sms_template`
  CHANGE COLUMN `channel_id` `vest_id` int(11) NULL DEFAULT NULL AFTER `agency_id`;

ALTER TABLE `dream_m_sms_template`
  DROP COLUMN `general`;

-- 标的添加分组id
ALTER TABLE `loan_u_product`
  ADD COLUMN `group_id` int(11) NULL DEFAULT NULL COMMENT '分组id' AFTER `agency_id`;

CREATE TABLE `loan_u_product_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `agency_id` int(11) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `status` tinyint(2) unsigned DEFAULT '0' COMMENT '状态 0禁用 1启用',
  `deleted` tinyint(2) unsigned DEFAULT '0' COMMENT '0未删除  1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;



-- 马甲添加标的分组id
ALTER TABLE `dream_u_app_vest`
  ADD COLUMN `group_id` int(11) NULL DEFAULT NULL AFTER `agency_id`,
  ADD COLUMN `push_sender_id` int(11) NULL DEFAULT NULL COMMENT '推送通道id' AFTER `group_id`;

-- 订单表添加标的分组id
ALTER TABLE `loan_u_order`
  ADD COLUMN `group_id` int(11) NULL DEFAULT NULL COMMENT '标的分组id' AFTER `product_id`;


-- 表结构调整
ALTER TABLE `dream_u_app_vest` ADD COLUMN `group_id` int(11) NULL DEFAULT NULL AFTER `agency_id`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `push_sender_id` int(11) NULL DEFAULT NULL COMMENT '推送通道id' AFTER `group_id`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `status` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用,0-否，1-是' AFTER `create_time`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `vest_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '马甲随机数' AFTER `status`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `refuse_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核拒绝\r\n\r\n审核拒绝\r\n\r\n审核拒绝状态，0-禁用，1-启用' AFTER `vest_code`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `refuse_jump_url` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核拒绝后的跳转链接' AFTER `refuse_status`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `pact_id` int(11) NULL DEFAULT NULL COMMENT '用户协议地址' AFTER `refuse_jump_url`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `vest_type` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认,0-否,1-是' AFTER `pact_id`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `identify`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `version`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `platform`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `vest`;

ALTER TABLE `dream_v_app_manager` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_a_agency_channel` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `channel_type`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `is_vest`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `about_owner`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `refuse_status`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `refuse_jump_url`;


ALTER TABLE `loan_s_actor_review` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_actor_review` ADD COLUMN `expire_new_count` int(8) NULL DEFAULT 0 COMMENT '新客超期订单量' AFTER `in_overdue_old_count`;

ALTER TABLE `loan_s_actor_review` ADD COLUMN `expire_old_count` int(8) NULL DEFAULT 0 COMMENT '老客超期订单量' AFTER `expire_new_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_review` ADD COLUMN `product_id` int(11) NULL DEFAULT NULL AFTER `channel_code`;

ALTER TABLE `loan_s_review` ADD COLUMN `product_group_id` int(11) NULL DEFAULT NULL AFTER `product_id`;

ALTER TABLE `loan_s_review` ADD COLUMN `expire_new_count` int(8) NULL DEFAULT 0 COMMENT '新客超期订单数' AFTER `review_old_fail_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `expire_old_count` int(8) NULL DEFAULT 0 COMMENT '老客超期订单数' AFTER `expire_new_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `machine_to_trans_new_count` int(8) NULL DEFAULT 0 COMMENT '新客机审到财务' AFTER `machine_old_fail_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `machine_to_trans_old_count` int(8) NULL DEFAULT 0 COMMENT '老客机审到财务' AFTER `machine_to_trans_new_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `first_overdue_new_count` int(8) NULL DEFAULT 0 COMMENT '新客首逾' AFTER `machine_to_trans_old_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `first_overdue_old_count` int(8) NULL DEFAULT 0 COMMENT '老客首逾' AFTER `first_overdue_new_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `in_overdue_new_count` int(8) NULL DEFAULT 0 COMMENT '新客在逾' AFTER `first_overdue_old_count`;

ALTER TABLE `loan_s_review` ADD COLUMN `in_overdue_old_count` int(8) NULL DEFAULT 0 COMMENT '老客在逾' AFTER `in_overdue_new_count`;

ALTER TABLE `loan_u_finance_io` MODIFY COLUMN `type` int(1) NULL DEFAULT NULL COMMENT '-1 放款 1还款  2自动扣款' AFTER `status`;

ALTER TABLE `loan_u_user_feedback` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `name`;

ALTER TABLE `risk_mould` MODIFY COLUMN `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间' AFTER `create_time`;

-- 放款统计添加马甲id
ALTER TABLE `loan_s_pay`
  ADD COLUMN `vest_id` int(11) NULL COMMENT '马甲id' AFTER `agency_id`;
-- 还款统计添加马甲
ALTER TABLE `loan_s_repay`
  ADD COLUMN `vest_id` int(11) NULL AFTER `agency_id`;


-- 用户行为统计表
ALTER TABLE `loan_s_user`
ADD COLUMN `vest_id` int(11) NULL COMMENT '马甲id' AFTER `channel_id`,
ADD COLUMN `channel_code` varchar(32) NULL COMMENT '渠道code' AFTER `vest_id`;

CREATE TABLE `loan_s_action_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NOT NULL,
  `vest_id` int(11) DEFAULT NULL COMMENT '马甲id',
  `channel_code` varchar(32) DEFAULT NULL COMMENT '渠道code',
  `action_date` date DEFAULT NULL COMMENT '统计日期',
  `uv_count` int(11) DEFAULT '0' COMMENT '渠道uv',
  `register_count` int(11) DEFAULT '0' COMMENT '注册人数',
  `login_count` int(11) DEFAULT '0' COMMENT '登录人数',
  `idcard_count` int(11) DEFAULT '0' COMMENT '实名认证人数',
  `face_count` int(11) DEFAULT '0' COMMENT '身份识别的数量',
  `info_count` int(11) DEFAULT '0' COMMENT '个人信息数量',
  `phone_count` int(11) DEFAULT '0' COMMENT '运营商认证数量',
  `ali_count` int(11) DEFAULT '0' COMMENT '淘宝认证数量',
  `bank_count` int(11) DEFAULT '0' COMMENT '绑卡数量',
  `order_count` int(11) DEFAULT '0' COMMENT '总提单',
  `new_count` int(11) DEFAULT '0' COMMENT '新客提单',
  `old_count` int(11) DEFAULT '0' COMMENT '老客提单',
  `pay_count` int(11) DEFAULT '0' COMMENT '放款数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB;



--  自动扣款中， 支持固定比例的扣款
ALTER TABLE `loan_a_finance_deduct` ADD COLUMN `deduct_type` int(11) NULL DEFAULT NULL COMMENT '扣款方式 1:按照比例扣款，2:固定额度扣款' AFTER `remark`;

ALTER TABLE `loan_a_finance_deduct` ADD COLUMN `deduct_config` varchar(128) NULL DEFAULT NULL COMMENT '扣款的配置' AFTER `deduct_type`;