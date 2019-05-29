package org.songbai.loan.statistic.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.user.OrderConstant.Guest;
import org.songbai.loan.constant.user.OrderConstant.Stage;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.statistic.ReviewOrderModel;
import org.songbai.loan.statistic.dao.ReviewOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 信审统计
 */
@Component
public class ReviewListener {
    private static final Logger logger = LoggerFactory.getLogger(ReviewListener.class);
    @Autowired
    ReviewOrderDao reviewOrderDao;

    @JmsListener(destination = JmsDest.REVIEW_STATIS)
    public void reviewStatis(JSONObject json) {
        if (json == null || json.get("agencyId") == null || json.get("stage") == null
                || json.get("guest") == null || json.get("status") == null || json.get("calcDate") == null
                || json.get("vestId") == null || json.get("channelCode") == null || json.get("productId") == null
                || json.get("productGroupId") == null) {
            logger.error(">>>>reviewStatis info is error,msg={}", json);
            return;
        }
        Integer stage = json.getInteger("stage");
        Integer agencyId = json.getInteger("agencyId");
        Integer guest = json.getInteger("guest");
        Integer status = json.getInteger("status");
        Integer vestId = json.getInteger("vestId");
        Integer productId = json.getInteger("productId");
        Integer productGroupId = json.getInteger("productGroupId");
        String channelCode = json.getString("channelCode");
        LocalDate calcDate = Date8Util.date2LocalDate(json.getDate("calcDate"));
        ReviewOrderModel info = reviewOrderDao.getInfoBy(agencyId, calcDate, vestId, channelCode, productId, productGroupId);

        ReviewOrderModel param = new ReviewOrderModel();

        if (stage == (Stage.MACHINE_AUTH.key)) {//机审
            if (status == Status.PROCESSING.key) {//机审通过到财务
                if (guest == Guest.NEW_GUEST.key) {
                    param.setMachineToTransNewCount(1);
                } else
                    param.setMachineToTransOldCount(1);
            } else if (status == Status.SUCCESS.key) {//成功到人审 成功到人审
                if (guest == Guest.NEW_GUEST.key) {
                    param.setMachineNewSuccCount(1);
                } else
                    param.setMachineOldSuccCount(1);
                param.setOrderWaitCount(1);
            } else if (status == Status.FAIL.key) { //失败
                if (guest == Guest.NEW_GUEST.key) {
                    param.setMachineNewFailCount(1);
                } else
                    param.setMachineOldFailCount(1);
            } else if (status == Status.WAIT.key) { //提单
                if (guest == Guest.NEW_GUEST.key) {
                    param.setOrderNewCount(1);
                } else param.setOrderOldCount(1);
                param.setOrderCount(1);
            } else if (status == Status.OVERDUE.key) {//机审失败转人审
                if (guest == Guest.NEW_GUEST.key) {
                    param.setMachineNewFailCount(-1);
                    param.setMachineNewSuccCount(1);
                } else {
                    param.setMachineOldFailCount(-1);
                    param.setMachineOldSuccCount(1);
                }
                param.setOrderWaitCount(1);
            }
        } else if (stage == Stage.ARTIFICIAL_AUTH.key) {//信审
            if (status == Status.SUCCESS.key) {
                if (guest == Guest.NEW_GUEST.key) {
                    param.setReviewNewSuccCount(1);
                } else
                    param.setReviewOldSuccCount(1);
                param.setOrderWaitCount(-1);
            } else if (status == Status.FAIL.key) {
                if (guest == Guest.NEW_GUEST.key) {
                    param.setReviewNewFailCount(1);
                } else
                    param.setReviewOldFailCount(1);
                param.setOrderWaitCount(-1);
            }
        } else if (stage == Stage.LOAN.key) {
            if (status == Status.OVERDUE.key) {//财务退回
                if (guest == Guest.NEW_GUEST.key) {
                    param.setReviewNewSuccCount(-1);
                } else
                    param.setReviewOldSuccCount(-1);
                param.setOrderWaitCount(1);
            } else if (status == Status.SUCCESS.key) {//超期订单统计
                if (guest == Guest.NEW_GUEST.key)
                    param.setExpireNewCount(1);
                else
                    param.setExpireOldCount(1);
            }
        } else if (stage == Stage.REPAYMENT.key) { //还款阶段
            if (status == Status.OVERDUE_LOAN.key || status == Status.CHASE_LOAN.key) {//逾期、催收
                if (guest.equals(Guest.NEW_GUEST.key)) {
                    param.setInOverdueNewCount(-1);
                } else param.setInOverdueOldCount(-1);
            } else if (status == Status.OVERDUE.key) {
                if (guest.equals(Guest.NEW_GUEST.key)) {
                    param.setFirstOverdueNewCount(1);
                    param.setInOverdueNewCount(1);
                } else {
                    param.setFirstOverdueOldCount(1);
                    param.setInOverdueOldCount(1);
                }
            }
        }
        if (info == null) {
            param.setCalcDate(calcDate);
            param.setAgencyId(agencyId);
            param.setVestId(vestId);
            param.setChannelCode(channelCode);
            param.setProductId(productId);
            param.setProductGroupId(productGroupId);
            reviewOrderDao.insert(param);
            return;
        }
        param.setId(info.getId());
        reviewOrderDao.updateReviewOrder(param);
    }
}
