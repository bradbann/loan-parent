-- 活动表
CREATE TABLE `loan_u_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `agency_id` int(11) DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '活动名称',
  `code` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '活动标识',
  `picture` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '图片地址',
  `url` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '活动链接地址',
  `status` tinyint(2) DEFAULT '0' COMMENT '0停用 1 启用',
  `deleted` tinyint(2) DEFAULT '0' COMMENT '0未删除1 已删除',
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 修改标的表
ALTER TABLE `loan_u_product`
  CHANGE COLUMN `stamp_rate` `stamp` double(18, 5) UNSIGNED NULL DEFAULT 0 COMMENT '综合费' AFTER `loan`,
  CHANGE COLUMN `url` `sorted` tinyint(2) UNSIGNED NULL DEFAULT NULL COMMENT '排序' AFTER `status`,
  MODIFY COLUMN `agency_id` int(11) NULL DEFAULT NULL AFTER `id`,
  MODIFY COLUMN `days` smallint(5) UNSIGNED NULL DEFAULT 0 COMMENT '借款期限(days)' AFTER `stamp`,
  MODIFY COLUMN `exceed_days` int(11) NULL DEFAULT 0 COMMENT '逾期计费天数' AFTER `type`,
  MODIFY COLUMN `status` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '状态 0禁用 1启用' AFTER `exceed_rate`,
  ADD COLUMN `pay` double(18, 5) UNSIGNED NULL DEFAULT 0 COMMENT '标的基础金额' AFTER `stamp`,
  ADD COLUMN `loan_count_min` int(11) NULL DEFAULT 0 COMMENT '借款次数最小' AFTER `exceed_rate`,
  ADD COLUMN `loan_count_max` int(11) NULL DEFAULT 0 COMMENT '借款次数最大' AFTER `loan_count_min`,
  ADD COLUMN `overdue_max` int(11) NULL DEFAULT 0 COMMENT '逾期天数最大' AFTER `overdue_min`,
  ADD COLUMN `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `sorted`,
  ADD COLUMN `deleted` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '0未删除  1删除' AFTER `status`,
  ADD COLUMN `is_default` tinyint(2) UNSIGNED NULL DEFAULT 0 COMMENT '1默认 0否' AFTER `deleted`,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`id`) USING BTREE;

-- 添加接入号字段
ALTER TABLE `dream_m_sms_sender`
  ADD COLUMN `data` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '短信配置其他参数' AFTER `status`;

-- app设置
CREATE TABLE `dream_v_app_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NULL DEFAULT NULL,
  `platform` tinyint(1) NOT NULL COMMENT '来源平台,1-安卓,2-ios',
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_qq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_wechat` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `copy_right` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- 渠道管理
CREATE TABLE `loan_a_actor_channel`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL,
  `channel_id` int(11) NOT NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- 渠道uv统计
ALTER TABLE `loan_s_user` ADD COLUMN `uv_count` int(11) NULL DEFAULT 0 COMMENT '渠道uv' AFTER `statistic_date`;

-- 渠道扣量
CREATE TABLE `loan_u_channel_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NULL DEFAULT NULL,
  `channel_id` int(11) NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `user_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- 更改银行卡表
ALTER TABLE `loan_u_finance_bank`
DROP COLUMN `chang_jie_code`,
ADD COLUMN `third_bank_code` varchar(25) NULL COMMENT '第三方支付公司银行代码' AFTER `bank_code`,
ADD COLUMN `pay_platform_id` tinyint(1) NULL DEFAULT NULL COMMENT '支付平台的id（1 畅捷，2 易宝）' AFTER `third_bank_code`;

-- 更改用户银行卡表
ALTER TABLE `loan_u_user_bankcard`
MODIFY COLUMN `bank_card_type` tinyint(1) NULL DEFAULT 1 COMMENT '0: 信用卡 1: 储蓄卡' AFTER `bank_card_num`,
ADD COLUMN `icon` varchar(255) NULL COMMENT '银行图标' AFTER `name`,
ADD COLUMN `type` tinyint(1) NULL COMMENT '0非默认 1默认' AFTER `bind_platform`,
DROP COLUMN `bank_id`,
DROP COLUMN `third_order_id`;

-- io表增加备注
ALTER TABLE `loan_u_finance_io`
ADD COLUMN `remark` varchar(255) NULL COMMENT '备注' AFTER `money`;

-- 修改config
ALTER TABLE `loan_u_finance_platform_config`
ADD COLUMN `status` tinyint(1) NULL DEFAULT 0 COMMENT '当前代理是否启用 0 禁用 1启用' AFTER `param`,
ADD COLUMN `bind` tinyint(1) NULL DEFAULT 0 COMMENT '0 不支持绑卡 1 支持绑卡' AFTER `status`;

-- 给还款表添加支付渠道
ALTER TABLE `loan_a_repayment_flow`
ADD COLUMN `pay_channel` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付渠道' AFTER `money`;

-- 代理支付设置
ALTER TABLE `dream_u_agency`
  ADD COLUMN `alipay_status` tinyint(2) NULL DEFAULT 0 COMMENT '1开 0 关' AFTER `jump_url`,
  ADD COLUMN `alipay_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址' AFTER `alipay_status`,
  ADD COLUMN `wepay_status` tinyint(2) NULL DEFAULT 0 COMMENT '1开 0 关' AFTER `alipay_url`,
  ADD COLUMN `wepay_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信地址' AFTER `wepay_status`,
  ADD COLUMN `h5_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 0 COMMENT 'H5支付开关' AFTER `wepay_url`,
  ADD COLUMN `h5_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'h5地址' AFTER `h5_status`,
  ADD COLUMN `auto_pay` tinyint(2) NULL DEFAULT 0 COMMENT '自动放款 0 否, 1 是' AFTER `h5_url`;

-- 加入我们
CREATE TABLE `loan_u_join` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(64) DEFAULT NULL COMMENT '手机号',
  `mail` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(2) unsigned DEFAULT '0' COMMENT '0未处理 1已处理',
  `actor_id` int(11) DEFAULT NULL COMMENT '处理人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB;

-- 添加市场
ALTER TABLE `loan_u_user`
  ADD COLUMN `market` varchar(64) NULL DEFAULT NULL COMMENT '渠道来源（小米商店、App Store）' AFTER `market_id`;


ALTER TABLE `loan_a_agency_channel` ADD COLUMN `is_vest` tinyint(1) NULL DEFAULT 0 COMMENT '是否马甲,0-否,1-是' AFTER `channel_type`;

ALTER TABLE `loan_a_agency_channel` ADD COLUMN `about_owner` int(11) NULL DEFAULT NULL COMMENT '关于我们, dream_v_app_manager.id' AFTER `is_vest`;

ALTER TABLE `loan_a_agency_channel` ADD COLUMN `refuse_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核拒绝状态,0-禁用，1-启用' AFTER `about_owner`;

ALTER TABLE `loan_a_agency_channel` ADD COLUMN `refuse_jump_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拒绝后的跳转链接' AFTER `refuse_status`;


ALTER TABLE `loan_a_repayment_flow` ADD COLUMN `pay_channel` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付渠道' AFTER `money`;


CREATE TABLE `dream_v_app_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NULL DEFAULT NULL,
  `platform` tinyint(1) NULL DEFAULT NULL COMMENT '来源平台,1-安卓,2-ios',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_qq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `customer_wechat` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `copy_right` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;


CREATE TABLE `loan_a_agency_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) NULL DEFAULT NULL,
  `create_id` int(11) NULL DEFAULT NULL,
  `amount` tinyint(10) NOT NULL COMMENT '金额',
  `fee_rate` double(6, 4) NOT NULL DEFAULT 1.0000 COMMENT '服务费利率',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;


CREATE TABLE `loan_area`  (
  `id` int(11) NULL DEFAULT NULL,
  `code` int(8) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `parent_id` int(11) NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `up_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `islow` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `addr_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `lvl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB;

CREATE TABLE `loan_area_bak`  (
  `id` int(11) NULL DEFAULT NULL,
  `code` int(8) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `parent_id` int(11) NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `up_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `islow` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `addr_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `lvl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB;