CREATE TABLE loan_s_actor_review_0122 LIKE loan_s_actor_review;
INSERT into loan_s_actor_review_0122 select * from loan_s_actor_review;
TRUNCATE TABLE loan_s_actor_review;
insert into loan_s_actor_review (agency_id,vest_id,actor_id,review_count,review_new_succ_count,review_old_succ_count,review_new_fail_count,review_old_fail_count,first_overdue_new_count,first_overdue_old_count,in_overdue_new_count,in_overdue_old_count,expire_new_count,expire_old_count,calc_date)
select
a.agency_id,a.vest_id,a.review_id,a.reviewCount,b.reviewNewSucc,b.reviewOldSucc,b.reviewNewFail,b.reviewOldFail,b.firstOverdueNew,b.firstOverdueOld,a.inOverdueNew,a.inOverdueOld,a.expireNew,a.expireOld,a.calcDate
from
(SELECT
	t.agency_id,u.vest_id,t.review_id,DATE_FORMAT( t.create_time, '%Y-%m-%d' ) calcDate,count(1) reviewCount,
	sum(IF(t.stage=4 and t.`status`=4 and t.guest=1,1,0)) inOverdueNew,
	sum(IF(t.stage=4 and t.`status`=4 and t.guest=3,1,0)) inOverdueOld,
		sum(IF(t.repayment_date <=DATE_FORMAT(date_sub(now(), interval 1 day), '%Y-%m-%d') and t.guest =1 ,1,0)) expireNew,
	sum(IF(t.repayment_date <=DATE_FORMAT(date_sub(now(), interval 1 day), '%Y-%m-%d') and t.guest =3 ,1,0)) expireOld
FROM
	loan_u_order t,
	loan_u_user u
WHERE
	t.user_id = u.id
	AND t.review_id IS NOT NULL
	group by t.agency_id,u.vest_id,t.review_id,calcDate ) a,

	(SELECT
	DATE_FORMAT( uo.create_time, '%Y-%m-%d' ) calcDate,
	t.agency_id,
	u.vest_id,
	uo.review_id,
	sum( IF ( t.stage = 2 AND t.`status` = 2 AND uo.guest = 1, 1, 0 ) ) reviewNewSucc,
	sum( IF ( t.stage = 2 AND t.`status` = 2 AND uo.guest = 3, 1, 0 ) ) reviewOldSucc,
	sum( IF ( t.stage = 2 AND t.`status` = 3 AND uo.guest = 3, 1, 0 ) ) reviewOldFail,
	sum( IF ( t.stage = 2 AND t.`status` = 3 AND uo.guest = 1, 1, 0 ) ) reviewNewFail,
	sum( IF ( t.stage = 4 AND t.`status` = 4 AND uo.guest = 1, 1, 0 ) ) firstOverdueNew,
	sum( IF ( t.stage = 4 AND t.`status` = 4 AND uo.guest = 3, 1, 0 ) ) firstOverdueOld
FROM
	loan_u_order_opt t,
	loan_u_user u,
	loan_u_order uo
WHERE
	t.user_id = u.id
	AND t.order_number = uo.order_number and uo.review_id is not null
GROUP BY
	calcDate,
	t.agency_id,
	u.vest_id,
	uo.review_id) b
	where a.agency_id = b.agency_id and a.calcDate = b.calcDate and a.review_id = b.review_id and a.vest_id = b.vest_id
	order by a.calcDate,a.agency_id,a.review_id