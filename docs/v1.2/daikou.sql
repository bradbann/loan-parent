-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库

CREATE TABLE `loan_a_finance_deduct`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `agency_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL COMMENT '代扣操作人id',
  `actor_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代扣操作人',
  `order_id` int(11) NULL DEFAULT NULL COMMENT '订单ID',
  `order_number` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `payment` double(20, 5) NULL DEFAULT NULL COMMENT '应还金额',
  `deduct_money` double(20, 5) NULL DEFAULT NULL COMMENT '已经代扣金额',
  `deduct_num` int(11) NOT NULL COMMENT '扣款次数',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '1:等待扣款， 2:扣款中， 3: 扣款结束，4:异常终止',
  `remark` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ;


CREATE TABLE `loan_a_finance_deduct_flow`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deduct_id` int(11) NOT NULL COMMENT '自动扣款委托',
  `deduct_level` int(11) NOT NULL COMMENT '自动扣款级别，又是扣款比例*100',
  `user_id` int(11) NOT NULL,
  `agency_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL COMMENT '代扣操作人id',
  `actor_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代扣操作人',
  `order_number` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `deduct_number` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代扣订单号',
  `name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名字',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `channel_id` int(11) NULL DEFAULT NULL COMMENT '渠道id',
  `vest_id` int(11) NULL DEFAULT NULL COMMENT '马甲id',
  `payment` double(20, 5) NULL DEFAULT NULL COMMENT '应还金额',
  `already_deduct` double(20, 5) NULL DEFAULT NULL COMMENT '已代扣金额',
  `deduct_money` double(20, 5) NULL DEFAULT NULL COMMENT '本次代扣金额',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '代扣状态 1等待，2成功，3失败',
  `pay_platform` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付平台名字',
  `bank_name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行名字',
  `bank_card_num` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `remark` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `repayment_date` date NULL DEFAULT NULL COMMENT '应还日期',
  `deduct_time` datetime(0) NULL DEFAULT NULL COMMENT '代扣时间',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ;



ALTER TABLE loan_u_order ADD COLUMN `charging_money` double(18, 5) NULL DEFAULT 0.00000 COMMENT '自动扣款金额' AFTER `deduct_money`;


INSERT INTO  `dream_m_push_template`(`id`, `name`, `type`, `sub_type`, `title`, `template`, `is_jump`, `create_time`, `update_time`) VALUES (8, '还款-扣款成功', 2, 8, '扣款成功', '您的借款已到期，系统已自动向银行卡扣款${money}元，点击查看>>>', 1, '2018-11-22 10:48:49', '2019-01-09 15:03:59');


ALTER TABLE `loan_a_repayment_flow`
  ADD COLUMN `auto_repayment` double(18, 5) UNSIGNED NULL DEFAULT 0.00000 COMMENT '代扣金额' AFTER `exceed_fee`;

ALTER TABLE `loan_a_repayment_flow`
  ADD COLUMN `payment` double(18, 5) UNSIGNED NULL DEFAULT NULL COMMENT '应还金额还款金额' AFTER `money`;

-- 应还金额修复
update  loan_a_repayment_flow set payment=money



