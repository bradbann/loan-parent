ALTER TABLE `risk_mould` ADD `default_sore` INT(11)  NOT NULL  DEFAULT '0'  COMMENT '默认得分'  AFTER `status`;
ALTER TABLE `risk_mould` ADD `score_type` INT(11)  NOT NULL  DEFAULT '0'  COMMENT '得分计算模式， -1 负分模式，0，默认模式， 1: 正分模式'  AFTER `default_sore`;

ALTER TABLE `risk_user_risk_order` ADD `risk_result_msg` VARCHAR(800)  NULL  DEFAULT NULL comment '风控原因' AFTER `risk_result_list`;
