package org.songbai.loan.model.statistic;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("loan_s_actor_review")
public class ActorReviewOrderModel {
    Integer id;
    Integer agencyId;
    Integer vestId;
    Integer actorId;
    Integer reviewCount;//审核单量
    Integer reviewNewSuccCount;//新客通过量
    Integer reviewOldSuccCount;//老客通过量
    Integer reviewNewFailCount;//新客拒绝量
    Integer reviewOldFailCount;//老客拒绝量
    Integer firstOverdueNewCount;//新客首逾订单量
    Integer firstOverdueOldCount;//老客首逾订单量
    Integer inOverdueNewCount;//新客逾期订单量
    Integer inOverdueOldCount;//老客逾期订单量
    Integer expireNewCount;//新客超期订单量
    Integer expireOldCount;//老客超期订单量
    LocalDate calcDate;//统计日期
    Date createTime;

}
