package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.user.OrderConstant;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ActorReviewVo {
    Integer agencyId;
    Integer actorId;
    Integer vestId;
    Integer reviewCount;//审核单量
    Integer reviewNewSuccCount;//新客通过量
    Integer reviewOldSuccCount;//老客通过量
    Integer reviewNewFailCount;//新客拒绝量
    Integer reviewOldFailCount;//老客拒绝量
    Integer firstOverdueNewCount;//新客首逾订单量
    Integer firstOverdueOldCount;//老客首逾订单量
    Integer inOverdueNewCount;//新客逾期订单量
    Integer inOverdueOldCount;//老客逾期订单量
    Integer expireNewCount;
    Integer expireOldCount;
    LocalDate calcDate;//统计日期
    Date createTime;

    //以下字段为页面展示的值
    String statisDate;//统计日期
    String actorName;
    String agencyName;
    String vestName;
    Integer succCount;//通过量
    String succRate = "0.00";//通过率
    Integer failCount;//审核拒绝量
    Integer firstOverdueCount;//首逾量
    String firstOverdueRate = "0.00";//首逾率
    Integer inOverdueCount;//在逾量
    String inOverdueRate = "0.00";//在逾率
    Integer expireCount;//超期订单数


    public ActorReviewVo calcRate(ActorReviewVo model, Integer guest) {
        ActorReviewVo vo = new ActorReviewVo();
        BeanUtil.copyNotNullProperties(model, vo);

        if (guest != null && guest == OrderConstant.Guest.NEW_GUEST.key) {
            vo.setReviewOldFailCount(0);
            vo.setReviewOldSuccCount(0);
            vo.setFirstOverdueOldCount(0);
            vo.setInOverdueOldCount(0);
            vo.setExpireOldCount(0);
        } else if (guest != null && guest == OrderConstant.Guest.OLD_GUEST.key) {
            vo.setReviewNewFailCount(0);
            vo.setReviewNewSuccCount(0);
            vo.setFirstOverdueNewCount(0);
            vo.setInOverdueNewCount(0);
            vo.setExpireNewCount(0);
        }

        vo.setSuccCount(vo.getReviewNewSuccCount() + vo.getReviewOldSuccCount());
        vo.setFailCount(vo.getReviewNewFailCount() + vo.getReviewOldFailCount());
        vo.setReviewCount(vo.getSuccCount() + vo.getFailCount());
        vo.setExpireCount(vo.getExpireNewCount() + vo.getExpireOldCount());
        vo.setFirstOverdueCount(vo.getFirstOverdueNewCount() + vo.getFirstOverdueOldCount());
        vo.setInOverdueCount(vo.getInOverdueNewCount() + vo.getInOverdueOldCount());

        if (vo.getReviewCount() > 0) {
            Double orderCount = vo.getReviewCount() / 100D;
            vo.setSuccRate(FormatUtil.formatDouble2(vo.getSuccCount() / orderCount));
        }
        if (vo.getExpireCount() > 0) {
            Double expireCount = vo.getExpireCount() / 100D;
            vo.setFirstOverdueRate(FormatUtil.formatDouble2(vo.getFirstOverdueCount() / expireCount));
            vo.setInOverdueRate(FormatUtil.formatDouble2(vo.getInOverdueCount() / expireCount));
        }

        return vo;
    }

}
