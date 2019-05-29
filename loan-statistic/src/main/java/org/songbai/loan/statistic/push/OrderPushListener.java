package org.songbai.loan.statistic.push;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.model.sms.PushGroupModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.statistic.dao.AdminVestDao;
import org.songbai.loan.statistic.dao.OrderDao;
import org.songbai.loan.statistic.model.po.OrderPO;
import org.songbai.loan.statistic.model.po.PushConditionPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * Author: qmw
 * Date: 2018/11/22 11:26 AM
 */
@Component
public class OrderPushListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderPushListener.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComPushTemplateService pushTemplateService;
    @Autowired
    private ComUserService comUserService;

    @Autowired
    private AdminVestDao vestDao;

    /**
     * 用户今日还款提醒推送
     */
    @JmsListener(destination = JmsDest.PUSH_ORDER_REPAY_REMIND)
    public void pushOrderRepayRemind() {

        if (logger.isInfoEnabled()) {
            logger.info("开始对今日要还款的用户进行提醒推送.....");
        }

        Set<Integer> vests = vestDao.findStartVestList();

        if (vests.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("推送>>>今日还款提醒没有推送的马甲.....");
            }
            return;
        }

        PushGroupModel groupModel = new PushGroupModel();

        PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.REPAY_REMIND);

        BeanUtil.copyNotNullProperties(pushModel, groupModel);

        groupModel.setVestIds(vests);
        groupModel.setScopes(Arrays.asList(1, 2));

        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_GROUP_MSG, groupModel);


    }

    /**
     * 用户今日还款短信提醒
     */
    @JmsListener(destination = JmsDest.PUSH_ORDER_REPAY_REMIND)
    public void sendMsgUserRepayOrderToday() {
        LocalDate today = LocalDate.now();
        List<Integer> vestIds = orderDao.findTodayRepayOrderAgencyId(today);
        for (Integer vestId : vestIds) {
            List<Integer> list = orderDao.findTodayRepayOrder(today, vestId, null);
            for (Integer userId : list) {
                UserModel model = comUserService.selectUserModelById(userId);
                if (model != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("phone", model.getPhone());
                    map.put("smsType", SmsConst.Type.REPAY_REMIND.value);
                    map.put("agencyId", model.getAgencyId());
                    map.put("channelId", model.getChannelId());
                    map.put("createTime", System.currentTimeMillis());
                    String message = JSON.toJSONString(map);
                    jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
                    logger.info("短信>>>今日还款提醒,发送通知,用户id={},phone={},message={}", userId, model.getPhone(), message);
                }
            }
        }
    }

    /**
     * 用户明日还款短信提醒
     */
    @JmsListener(destination = JmsDest.PUSH_ORDER_REPAY_REMIND_TOMORROW)
    public void sendMsgUserRepayOrdertTomorrow() {
        logger.info("短信>>>执行明日还款提醒");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Integer> vestIds = orderDao.findTodayRepayOrderAgencyId(tomorrow);
        for (Integer vestId : vestIds) {
            List<Integer> list = orderDao.findTodayRepayOrder(tomorrow, vestId, null);
            for (Integer userId : list) {
                UserModel model = comUserService.selectUserModelById(userId);
                if (model != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("phone", model.getPhone());
                    map.put("smsType", SmsConst.Type.TOMORROW_REPAY_REMIND.value);
                    map.put("agencyId", model.getAgencyId());
                    map.put("channelId", model.getChannelId());
                    map.put("createTime", System.currentTimeMillis());
                    String message = JSON.toJSONString(map);
                    jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
                    logger.info("短信>>>明日还款提醒, 发送通知,用户id={},phone={},message={}", userId, model.getPhone(), message);
                }
            }
        }
    }

    /**
     * 用户逾期推送
     */
    @JmsListener(destination = JmsDest.PUSH_ORDER_OVERDUE)
    public void pushOrderOverdue() {
        if (logger.isInfoEnabled()) {
            logger.info("开始对逾期用户进行推送.....");
        }

        Set<Integer> vests = vestDao.findStartVestList();
        if (vests.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("推送>>>逾期推送没有有效马甲个推账号.....");
            }
            return;
        }

        List<PushConditionPO> conditions = orderDao.findOrderOverdueDaysAndFee();

        if (conditions.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("推送>>>逾期推送逾期天数和逾期费用条件集合.....");
            }
            return;
        }

        for (PushConditionPO condition : conditions) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("day", condition.getExceedDays());
            jsonObject.put("money", FormatUtil.formatDouble2(condition.getExceedFee()));

            PushGroupModel groupModel = new PushGroupModel();
            PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.LOAN_OVERDUE, jsonObject);
            BeanUtil.copyNotNullProperties(pushModel, groupModel);

            groupModel.setVestIds(vests);
            groupModel.setCondition(jsonObject);
            groupModel.setScopes(Arrays.asList(1, 2));
            jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_GROUP_MSG, groupModel);

        }

    }
}
