ALTER TABLE `dream_a_actor`
ADD COLUMN `is_validate` tinyint(1) NULL DEFAULT 1 COMMENT '是否需要验证码,0-否,1-是' AFTER `is_manager`;

ALTER TABLE `dream_u_agency`
  ADD COLUMN `bad_debt` tinyint(2) UNSIGNED NULL DEFAULT 30 COMMENT '自动坏账天数 默认30' AFTER `auto_pay`;