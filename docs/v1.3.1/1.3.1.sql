
ALTER TABLE `dream_u_agency`
MODIFY COLUMN `mould_id` int(11) NULL DEFAULT NULL COMMENT '新客风控模型id' AFTER `agency_level`,
ADD COLUMN `old_guest_mould_id` int(11) NULL COMMENT '老客风控模型id' AFTER `mould_id`;

-- 落地页管理
CREATE TABLE `dream_v_floor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agency_id` int(11) DEFAULT NULL,
  `floor_name` varchar(64) DEFAULT NULL COMMENT '落地页名称',
  `floor_url` varchar(255) DEFAULT NULL COMMENT '落地页地址',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;


-- 用户表添加删除字段
ALTER TABLE `loan_u_user`
  ADD COLUMN `deleted` tinyint(2) NULL DEFAULT 0 COMMENT '0未删除 1已删除' AFTER `guest`,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`id`) USING BTREE;

-- 版本控制添加字段
ALTER TABLE `dream_v_version`
ADD COLUMN `vest_id` int(11) NULL COMMENT '马甲id' AFTER `agency_id`,
ADD COLUMN `channel_code` varchar(255) NULL COMMENT '渠道code' AFTER `vest_id`;

-- 用户认证表添加删除字段
ALTER TABLE `loan_u_user_info`
  ADD COLUMN `deleted` tinyint(2) NULL DEFAULT 0 COMMENT '0未删除' AFTER `other_phone`,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`user_id`) USING BTREE;

-- 用户绑卡表添加删除字段
ALTER TABLE `loan_u_user_bankcard`
  ADD COLUMN `deleted` tinyint(2) NULL DEFAULT 0 COMMENT '0未删除' AFTER `status`,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`id`) USING BTREE;