package org.songbai.loan.statistic.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.user.OrderConstant.Guest;
import org.songbai.loan.constant.user.OrderConstant.Stage;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.statistic.ActorReviewOrderModel;
import org.songbai.loan.statistic.dao.ActorReviewOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 信审人员统计
 */
@Component
public class ActorOrderStatisListener {
    private static final Logger logger = LoggerFactory.getLogger(ActorOrderStatisListener.class);
    @Autowired
    ActorReviewOrderDao actorReviewOrderDao;


    @JmsListener(destination = JmsDest.ACTOR_REVIEW_STATIS)
    public void actorReviewStatis(JSONObject json) {
        if (json == null || json.get("agencyId") == null || json.get("actorId") == null
                || json.get("stage") == null || json.get("guest") == null || json.get("status") == null
                || json.get("calcDate") == null || json.get("vestId") == null) {
            logger.error(">>>>actorReviewStatis info is error,msg={}", json);
            return;
        }
        Integer stage = json.getInteger("stage");
        Integer agencyId = json.getInteger("agencyId");
        Integer guest = json.getInteger("guest");
        Integer status = json.getInteger("status");
        Integer actorId = json.getInteger("actorId");
        Integer vestId = json.getInteger("vestId");
        LocalDate calcDate = Date8Util.date2LocalDate(json.getDate("calcDate"));
        ActorReviewOrderModel model = actorReviewOrderDao.getInfoByAgencyId(agencyId, calcDate, actorId, vestId);
        ActorReviewOrderModel param = new ActorReviewOrderModel();
        if (stage.equals(Stage.ARTIFICIAL_AUTH.key)) {//复审阶段
            if (status.equals(Status.SUCCESS.key)) {//成功
                if (guest.equals(Guest.NEW_GUEST.key)) {
                    param.setReviewNewSuccCount(1);
                } else param.setReviewOldSuccCount(1);
                param.setReviewCount(1);
            } else if (status == Status.FAIL.key) {
                if (guest.equals(Guest.NEW_GUEST.key)) {//信审失败
                    param.setReviewNewFailCount(1);
                } else param.setReviewOldFailCount(1);
                param.setReviewCount(1);
            }
        } else if (stage == Stage.LOAN.key) {
            if (status == Status.OVERDUE.key) {//财务退回
                param.setReviewCount(-1);
                if (guest == Guest.NEW_GUEST.key) {
                    param.setReviewNewSuccCount(-1);
                } else
                    param.setReviewOldSuccCount(-1);
            } else if (status == Status.SUCCESS.key) {//超期订单统计
                if (guest == Guest.NEW_GUEST.key)
                    param.setExpireNewCount(1);
                else
                    param.setExpireOldCount(1);
            }

        } else if (stage.equals(Stage.REPAYMENT.key)) { //还款阶段
            if (status.equals(Status.OVERDUE_LOAN.key) || status.equals(Status.CHASE_LOAN.key)) {//逾期、催收
                if (guest.equals(Guest.NEW_GUEST.key)) {
                    param.setInOverdueNewCount(-1);
                } else param.setInOverdueOldCount(-1);
            } else if (status.equals(Status.OVERDUE.key)) {
                if (guest.equals(Guest.NEW_GUEST.key)) {
                    param.setFirstOverdueNewCount(1);
                    param.setInOverdueNewCount(1);
                } else {
                    param.setFirstOverdueOldCount(1);
                    param.setInOverdueOldCount(1);
                }
            }
        }
        if (model == null) {
            param.setCalcDate(calcDate);
            param.setAgencyId(agencyId);
            param.setActorId(actorId);
            param.setVestId(vestId);
            actorReviewOrderDao.insert(param);
            return;
        }
        param.setId(model.getId());
        actorReviewOrderDao.updateActorReviewInfo(param);
    }

}
