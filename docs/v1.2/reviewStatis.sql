-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库
-- 先备份数据库

--先备份
CREATE TABLE loan_s_review_0122 LIKE loan_s_review;
INSERT into loan_s_review_0122 select * from loan_s_review;

--再清表
TRUNCATE TABLE loan_s_review;

--修复信审统计数据
INSERT INTO loan_s_review (agency_id,vest_id,channel_code,product_id,product_group_id,order_count,order_new_count,order_old_count,order_wait_count,review_new_succ_count,review_old_succ_count,review_new_fail_count,review_old_fail_count,expire_new_count,expire_old_count,machine_new_succ_count,machine_old_succ_count,machine_new_fail_count,machine_old_fail_count,machine_to_trans_new_count,machine_to_trans_old_count,first_overdue_new_count,first_overdue_old_count,in_overdue_new_count,in_overdue_old_count,calc_date)
select a.agency_id,a.vest_id,a.channel_code,a.product_id,a.group_id,
a.orderCount,a.newOrder,a.oldOrder,a.waitOrder,b.reviewNewSucc,b.reviewOldSucc,b.reviewNewFail,
b.reviewOldFail,a.expireNew,a.expireOld,b.machineNewSucc,b.machineOldSucc,b.machineNewFail,b.machineOldFail,
b.machineToTransNew,b.machineToTransOld,b.firstOverdueNew,b.firstOverdueOld,a.inOverdueNew,a.inOverdueOld,a.calcDate
from
(SELECT
	DATE_FORMAT( t.create_time, '%Y-%m-%d' ) calcDate,
	t.agency_id,
	u.vest_id,
	u.channel_code,
	t.product_id,
	t.group_id,
	count( 1 ) orderCount,
	sum( IF ( t.guest = 1, 1, 0 ) ) newOrder,
	sum( IF ( t.guest = 3, 1, 0 ) ) oldOrder,
	sum( IF ( t.stage = 2 AND t.`status` = 1, 1, 0 ) ) waitOrder ,
	sum(IF(t.repayment_date <=DATE_FORMAT(date_sub(now(), interval 1 day), '%Y-%m-%d') and t.guest =1 ,1,0)) expireNew,
	sum(IF(t.repayment_date <=DATE_FORMAT(date_sub(now(), interval 1 day), '%Y-%m-%d') and t.guest =3 ,1,0)) expireOld,
	sum(IF(t.stage=4 and t.`status`=4 and t.guest=1,1,0)) inOverdueNew,
	sum(IF(t.stage=4 and t.`status`=4 and t.guest=3,1,0)) inOverdueOld
FROM
	loan_u_order t,
	loan_u_user u
WHERE
	t.user_id = u.id
GROUP BY
	calcDate,
	agency_id,
	u.vest_id,
	u.channel_code,
	t.product_id,
	t.group_id ) a,(SELECT
	DATE_FORMAT( uo.create_time, '%Y-%m-%d' ) calcDate,
	t.agency_id,
	u.vest_id,
	u.channel_code,
	uo.product_id,
	uo.group_id,
	sum( IF ( t.stage = 2 AND t.`status` = 2 AND uo.guest = 1, 1, 0 ) ) reviewNewSucc,
	sum( IF ( t.stage = 2 AND t.`status` = 2 AND uo.guest = 3, 1, 0 ) ) reviewOldSucc,
	sum( IF ( t.stage = 2 AND t.`status` = 3 AND uo.guest = 1, 1, 0 ) ) reviewNewFail,
	sum( IF ( t.stage = 2 AND t.`status` = 3 AND uo.guest = 3, 1, 0 ) ) reviewOldFail,
	sum(IF(t.stage=1 and t.`status`=2 and uo.guest =1 ,1,0)) machineNewSucc,
	sum(IF(t.stage=1 and t.`status`=2 and uo.guest =3 ,1,0)) machineOldSucc,
	sum(IF(t.stage=1 and t.`status`=3 and uo.guest =3 ,1,0)) machineOldFail,
	sum(IF(t.stage=1 and t.`status`=3 and uo.guest =1 ,1,0)) machineNewFail,
	sum(IF(t.stage=1 and t.`status`=0 and uo.guest =1 ,1,0)) machineToTransNew,
	sum(IF(t.stage=1 and t.`status`=0 and uo.guest =3 ,1,0)) machineToTransOld,
	sum(IF(t.stage=4 and t.`status`=4 and uo.guest =1 ,1,0)) firstOverdueNew,
	sum(IF(t.stage=4 and t.`status`=4 and uo.guest =3 ,1,0)) firstOverdueOld
FROM
	loan_u_order_opt t,
	loan_u_user u,
	loan_u_order uo
WHERE
	t.user_id = u.id
	AND t.order_number = uo.order_number
	group by calcDate,
	t.agency_id,
	u.vest_id,
	u.channel_code,
	uo.product_id,
	uo.group_id) b
	where a.agency_id = b.agency_id and a.calcDate = b.calcDate and a.channel_code = b.channel_code
	and a.product_id = b.product_id and a.vest_id = b.vest_id
	ORDER BY a.calcDate ,a.agency_id;