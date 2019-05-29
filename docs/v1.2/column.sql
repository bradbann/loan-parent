-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库

ALTER TABLE `dream_m_push_sender` ADD COLUMN `id` int(11) NOT NULL COMMENT '主键id' FIRST;

ALTER TABLE `dream_m_push_sender` ADD COLUMN `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '推送名称' AFTER `agency_id`;

ALTER TABLE `dream_m_push_sender` ADD COLUMN `deleted` tinyint(2) NULL DEFAULT 0 COMMENT '0未 1删除 ' AFTER `status`;

ALTER TABLE `dream_m_push_sender` MODIFY COLUMN `agency_id` int(11) NULL DEFAULT NULL COMMENT '代理id' AFTER `id`;

ALTER TABLE `dream_m_push_sender` DROP PRIMARY KEY;

ALTER TABLE `dream_m_push_sender` ADD PRIMARY KEY (`id`) USING BTREE;

ALTER TABLE `dream_m_push_sender` MODIFY COLUMN `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id' FIRST;

ALTER TABLE `dream_m_sms_sender` MODIFY COLUMN `password` varchar(100) NOT NULL DEFAULT '1' COMMENT '账号' AFTER `agency_id`;

ALTER TABLE `dream_m_sms_sender` MODIFY COLUMN `account` varchar(100) NULL DEFAULT NULL COMMENT '密码' AFTER `password`;

ALTER TABLE `dream_m_sms_sender` MODIFY COLUMN `type` int(2) NOT NULL COMMENT '通道类型 5 创蓝 6 泡泡云' AFTER `account`;

ALTER TABLE `dream_m_sms_sender` MODIFY COLUMN `status` int(11) NOT NULL DEFAULT 1 COMMENT '激活状态 默认为1 表示未激活 0表示已激活' AFTER `type`;

ALTER TABLE `dream_m_sms_sender` DROP COLUMN `category`;

ALTER TABLE `dream_m_sms_sender` DROP COLUMN `super_id`;

ALTER TABLE `dream_m_sms_template` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL AFTER `agency_id`;

ALTER TABLE `dream_m_sms_template` MODIFY COLUMN `deleted` int(1) NOT NULL DEFAULT 0 COMMENT '表示是否删除  1删除  0未删除' AFTER `status`;

ALTER TABLE `dream_m_sms_template` DROP COLUMN `channel_id`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `group_id` int(11) NULL DEFAULT NULL AFTER `agency_id`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `push_sender_id` int(11) NULL DEFAULT NULL COMMENT '推送通道id' AFTER `group_id`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `status` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用,0-否，1-是' AFTER `create_time`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `vest_code` varchar(32) NULL DEFAULT NULL COMMENT '马甲随机数' AFTER `status`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `refuse_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核拒绝\r\n\r\n审核拒绝\r\n\r\n审核拒绝状态，0-禁用，1-启用' AFTER `vest_code`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `refuse_jump_url` varchar(64) NULL DEFAULT NULL COMMENT '审核拒绝后的跳转链接' AFTER `refuse_status`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `pact_id` varchar(64) NULL DEFAULT NULL COMMENT '用户协议地址' AFTER `refuse_jump_url`;

ALTER TABLE `dream_u_app_vest` ADD COLUMN `vest_type` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认,0-否,1-是' AFTER `pact_id`;

ALTER TABLE `dream_u_app_vest` MODIFY COLUMN `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP AFTER `push_sender_id`;

ALTER TABLE `dream_u_app_vest` MODIFY COLUMN `platform` tinyint(4) NULL DEFAULT 3 COMMENT '来源,1-android,2-ios,3-全部' AFTER `vest_type`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `identify`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `version`;

ALTER TABLE `dream_u_app_vest` DROP COLUMN `vest`;

ALTER TABLE `dream_v_app_manager` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `dream_v_app_manager` ADD COLUMN `title` varchar(255) NULL DEFAULT NULL COMMENT '标题' AFTER `platform`;

ALTER TABLE `loan_a_agency_channel` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `channel_type`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `is_vest`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `about_owner`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `refuse_status`;

ALTER TABLE `loan_a_agency_channel` DROP COLUMN `refuse_jump_url`;

ALTER TABLE `loan_a_repayment_flow` MODIFY COLUMN `type` tinyint(2) NULL DEFAULT NULL COMMENT '1线上还款 2线下还款  3自动扣款' AFTER `auto_repayment`;

CREATE TABLE `loan_s_action_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NOT NULL,
  `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id',
  `channel_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道code',
  `action_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `uv_count` int(11) NULL DEFAULT 0 COMMENT '渠道uv',
  `register_count` int(11) NULL DEFAULT 0 COMMENT '注册人数',
  `login_count` int(11) NULL DEFAULT 0 COMMENT '登录人数',
  `idcard_count` int(11) NULL DEFAULT 0 COMMENT '实名认证人数',
  `face_count` int(11) NULL DEFAULT 0 COMMENT '身份识别的数量',
  `info_count` int(11) NULL DEFAULT 0 COMMENT '个人信息数量',
  `phone_count` int(11) NULL DEFAULT 0 COMMENT '运营商认证数量',
  `ali_count` int(11) NULL DEFAULT 0 COMMENT '淘宝认证数量',
  `bank_count` int(11) NULL DEFAULT 0 COMMENT '绑卡数量',
  `order_count` int(11) NULL DEFAULT 0 COMMENT '总提单',
  `new_count` int(11) NULL DEFAULT 0 COMMENT '新客提单',
  `old_count` int(11) NULL DEFAULT 0 COMMENT '老客提单',
  `pay_count` int(11) NULL DEFAULT 0 COMMENT '放款数量',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;

ALTER TABLE `loan_s_actor_review` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_actor_review` ADD COLUMN `expire_new_count` int(8) NULL DEFAULT 0 COMMENT '新客超期订单量' AFTER `in_overdue_old_count`;

ALTER TABLE `loan_s_actor_review` ADD COLUMN `expire_old_count` int(8) NULL DEFAULT 0 COMMENT '老客超期订单量' AFTER `expire_new_count`;

ALTER TABLE `loan_s_pay` DROP INDEX `idx_u_agency_id_and_pay_date`;

ALTER TABLE `loan_s_pay` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_pay` ADD UNIQUE INDEX `idx_u_agency_id_and_pay_date`(`agency_id`, `pay_date`, `vest_id`) USING BTREE COMMENT 'agency_id&pay_date唯一索引';

ALTER TABLE `loan_s_repay` DROP INDEX `idx_u_agency_id_and_repay_date`;

ALTER TABLE `loan_s_repay` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL AFTER `agency_id`;

ALTER TABLE `loan_s_repay` ADD UNIQUE INDEX `idx_u_agency_id_and_repay_date`(`agency_id`, `repay_date`, `vest_id`) USING BTREE COMMENT 'agency_id&repay_date唯一索引';

ALTER TABLE `loan_s_review` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_review` ADD COLUMN `channel_code` varchar(64) NULL DEFAULT NULL COMMENT '渠道code' AFTER `vest_id`;

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

ALTER TABLE `loan_s_user` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `agency_id`;

ALTER TABLE `loan_s_user` ADD COLUMN `channel_code` varchar(32) NULL DEFAULT NULL COMMENT '渠道code' AFTER `vest_id`;

ALTER TABLE `loan_u_activity` ADD COLUMN `scopes` varchar(255) NULL DEFAULT NULL COMMENT '平台范围多个使用,分割，并且前后必要有空格' AFTER `remark`;

ALTER TABLE `loan_u_activity` ADD COLUMN `vestlist` varchar(255) NULL DEFAULT NULL COMMENT '马甲包ID, 多个使用,分割，并且前后必要有空格' AFTER `scopes`;

ALTER TABLE `loan_u_finance_io` MODIFY COLUMN `type` int(1) NULL DEFAULT NULL COMMENT '-1 放款 1还款  2自动扣款' AFTER `status`;

ALTER TABLE `loan_u_finance_platform_config` MODIFY COLUMN `param` text NULL COMMENT '支付/转账参数' AFTER `platform_id`;

ALTER TABLE `loan_u_order` ADD COLUMN `group_id` int(11) NULL DEFAULT NULL COMMENT '标的分组id' AFTER `product_id`;

ALTER TABLE `loan_u_product` ADD COLUMN `group_id` int(11) NULL DEFAULT NULL COMMENT '分组id' AFTER `agency_id`;

ALTER TABLE `loan_u_product` MODIFY COLUMN `deleted` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '0未删除  1删除' AFTER `is_default`;

CREATE TABLE `loan_u_product_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `agency_id` int(11) NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '状态 0禁用 1启用',
  `deleted` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '0未删除  1删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ;

ALTER TABLE `loan_u_user` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `channel_id`;

ALTER TABLE `loan_u_user` ADD COLUMN `vest_code` varchar(32) NULL DEFAULT NULL COMMENT '马甲随机数' AFTER `vest_id`;

ALTER TABLE `loan_u_user` ADD COLUMN `channel_code` varchar(64) NULL DEFAULT NULL COMMENT '渠道来源（小米商店、App Store）' AFTER `vest_code`;

ALTER TABLE `loan_u_user` DROP COLUMN `market_id`;

ALTER TABLE `loan_u_user` DROP COLUMN `market`;

ALTER TABLE `loan_u_user_bankcard` MODIFY COLUMN `request_id` varchar(50) DEFAULT NULL COMMENT '请求易宝的id，相当于绑卡订单号，可以根据此查绑卡信息' AFTER `agency_id`;

ALTER TABLE `loan_u_user_bankcard` MODIFY COLUMN `bank_code` varchar(50) NULL DEFAULT NULL COMMENT '平台的银行卡code' AFTER `bank_name`;

ALTER TABLE `loan_u_user_bankcard` MODIFY COLUMN `bank_card_type` tinyint(1) NULL DEFAULT 1 COMMENT '0:不能识别; 1: 借记卡; 2: 信用卡' AFTER `bank_card_num`;

ALTER TABLE `loan_u_user_bankcard` MODIFY COLUMN `status` tinyint(1) NULL DEFAULT 0 COMMENT '0待绑定  1已绑定 2 已解绑' AFTER `type`;

ALTER TABLE `loan_u_user_feedback` ADD COLUMN `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id' AFTER `name`;

ALTER TABLE `loan_u_user_info` ADD COLUMN `first_rela` varchar(50) NULL DEFAULT NULL COMMENT '直接联系人关系' AFTER `address_time`;

ALTER TABLE `loan_u_user_info` ADD COLUMN `other_rela` varchar(50) NULL DEFAULT NULL COMMENT '其他联系人关系' AFTER `first_phone`;

ALTER TABLE `loan_u_user_info` MODIFY COLUMN `education` varchar(50) NULL DEFAULT NULL COMMENT '学历' AFTER `company_address`;

ALTER TABLE `loan_u_user_info` MODIFY COLUMN `address` varchar(255)  NULL DEFAULT NULL COMMENT '用户现居地址' AFTER `education`;

ALTER TABLE `loan_u_user_info` MODIFY COLUMN `first_phone` varchar(50) NULL DEFAULT NULL COMMENT '电话' AFTER `first_contact`;

ALTER TABLE `risk_mould` MODIFY COLUMN `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间' AFTER `create_time`;

DROP TABLE `loan_u_join`;
