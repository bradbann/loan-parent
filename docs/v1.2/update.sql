-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库



--字典表新增pact_type 协议类型
INSERT into dream_a_dictionary (type,name,code,value,create_user,update_user)VALUES('pact_type','用户协议','user_pact',1,'admin','admin');
--手动往马甲表新增一条0的默认数据

--修改渠道表默认渠道code为default
update loan_a_agency_channel set channel_code = 'default' where channel_type = 1;

--创建代理默认马甲
INSERT into dream_u_app_vest (`name`,agency_id,vest_code,vest_type)
select '默认马甲',t.id,'default',1 from dream_u_agency t where not EXISTS (select 1 from dream_u_app_vest where vest_type = 1 and agency_id = t.id);

--更新用户表马甲字段
update loan_u_user t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 ),t.vest_code = (select vest_code from dream_u_app_vest where agency_id = t.agency_id and vest_type =1);

--更新信审统计的数据
update loan_s_review t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 ),t.channel_code = (select channel_code from loan_a_agency_channel where agency_id = t.agency_id and channel_type =1);

--更新信审人员统计
update loan_s_actor_review t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 ) ;

--
update dream_v_app_manager t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 ) ;

-- 用户表马甲id和渠道code
update loan_u_user u,loan_a_agency_channel c  set u.channel_code=c.channel_code
where  u.channel_id=c.id;
#
#
#
# update loan_u_user u,dream_u_app_vest c  set u.vest_code=c.vest_code ,u.vest_id=c.id
# where  u.channel_id=c.id;


update loan_s_user t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 ) ;

ALTER TABLE `loan_s_pay`
  DROP INDEX `idx_u_agency_id_and_pay_date`,
  ADD UNIQUE INDEX `idx_u_agency_id_and_pay_date`(`agency_id`, `pay_date`, `vest_id`) USING BTREE COMMENT 'agency_id&pay_date唯一索引';

ALTER TABLE  `loan_s_repay`
  DROP INDEX `idx_u_agency_id_and_repay_date`,
  ADD UNIQUE INDEX `idx_u_agency_id_and_repay_date`(`agency_id`, `repay_date`, `vest_id`) USING BTREE COMMENT 'agency_id&repay_date唯一索引';

--更新渠道默认马甲
update loan_a_agency_channel t set t.vest_id = (select id from dream_u_app_vest where agency_id = t.agency_id and vest_type =1 )  where t.vest_id is null ;

-- 数据修复完成才能删除
ALTER TABLE `loan_s_user`
  DROP COLUMN `channel_id`;



--  自动扣款中， 支持固定比例的扣款
update loan_a_finance_deduct set deduct_type = 1 , deduct_config= '50,25' where  1 =1