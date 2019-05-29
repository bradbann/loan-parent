CREATE TABLE `dream_m_voice_sender` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '渠道名称',
  `agency_id` int(11) NOT NULL,
  `type` int(2) NOT NULL COMMENT '1 luosimao',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '1 启用 0停用',
  `data` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '发送数据配置其他参数',
  `is_delete` int(11) NOT NULL DEFAULT '0' COMMENT '表示删除状态 1未删除 0为删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `dream_u_agency` ADD COLUMN `jhpay_merid` varchar(32) NULL DEFAULT NULL COMMENT '汇潮支付账号' AFTER `bad_debt`;

ALTER TABLE `dream_u_agency` ADD COLUMN `jhpay_key` varchar(64) NULL DEFAULT NULL COMMENT '汇潮支付密码' AFTER `jhpay_merid`;


-- 智能外呼
CREATE TABLE `dream_m_call_sender` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `agency_id` int(11) NOT NULL,
  `vest_id` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '渠道名称',
  `app_id` varchar(100) NOT NULL DEFAULT '1' COMMENT '账号',
  `app_key` varchar(100) DEFAULT NULL COMMENT '密码',
  `data` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '外呼参数其他配置',
  `type` int(2) NOT NULL COMMENT '1 深市智能',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '激活状态 默认为1 表示未激活 0表示已激活',
  `remark` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '备注',
  `url` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT 'api访问接口',
  `is_delete` int(11) NOT NULL DEFAULT '0' COMMENT '表示删除状态 1未删除 0为删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;