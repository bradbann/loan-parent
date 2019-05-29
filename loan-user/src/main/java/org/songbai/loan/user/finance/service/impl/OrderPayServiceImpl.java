package org.songbai.loan.user.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.loan.RepaymentFlowModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.dao.RepaymentFlowDao;
import org.songbai.loan.user.finance.service.OrderPayService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.OrderOptDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.songbai.loan.constant.user.OrderConstant.Status;
import static org.songbai.loan.constant.user.OrderConstant.Status.*;

@Service
@Slf4j
public class OrderPayServiceImpl implements OrderPayService {


    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RepaymentFlowDao repaymentFlowDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private FinanceIODao ioDao;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserBankCardDao bankCardDao;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComPushTemplateService pushTemplateService;
    @Autowired
    StatisSendHelper statisSendHelper;

    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceImpl.class);

    @Transactional
    @Override
    public void payOrderForDeduct(FinanceIOModel ioModel) {
        Integer userId = ioModel.getUserId();
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), userId);

        // 先把金额修改掉
        orderDao.updateOrderForDeduct(orderModel.getId(), ioModel.getMoney());

        orderModel = orderDao.selectById(orderModel.getId());

        // 然后判断用户是否还完了。
        boolean finish = updateOrderStatusForRepayment(orderModel);

        UserModel userModel = comUserService.selectUserModelById(userId);
        if (finish && userModel.getGuest() != OrderConstant.Guest.OLD_GUEST.key) {
            //更新user表
            UserModel updateUserModel = new UserModel();
            updateUserModel.setId(userModel.getId());
            updateUserModel.setGuest(OrderConstant.Guest.OLD_GUEST.key);
            userDao.updateById(updateUserModel);

            userModel.setGuest(OrderConstant.Guest.OLD_GUEST.key);
            redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, userModel.getId());
        }

        //更新opt表
//        OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(orderModel.getOrderNumber(), orderModel.getUserId());
        OrderOptModel updateOrderOptModel = new OrderOptModel();
        updateOrderOptModel.setStatus(finish ? orderModel.getStatus() : Status.AUTO_DEDUCT.key);
        updateOrderOptModel.setRemark(finish ? "自动扣款还款成功，扣款金额：" + ioModel.getMoney() : "自动扣款部分还款，扣款金额：" + ioModel.getMoney());
        updateOrderOptModel.setGuest(userModel.getGuest());
        updateOrderOptModel.setUserId(userId);
        updateOrderOptModel.setAgencyId(orderModel.getAgencyId());
        updateOrderOptModel.setOrderNumber(orderModel.getOrderNumber());
        updateOrderOptModel.setStatus(Status.AUTO_DEDUCT.key);
        updateOrderOptModel.setStage(orderModel.getStage());
        updateOrderOptModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);

        orderOptDao.insert(updateOrderOptModel);



        Map<String, Object> param = new HashMap<>();
        param.put("money", FormatUtil.formatDouble2(ioModel.getMoney()));

        Map<String, Object> map = new HashMap<>();
        map.put("phone", userModel.getPhone());
        map.put("smsType", SmsConst.Type.AUTO_DEDUCT.value);
        map.put("agencyId", userModel.getAgencyId());
        map.put("param", param);
        map.put("channelId", userModel.getChannelId());
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
        logger.info("短信>>>自动扣款,发送通知,用户id={},phone={},message={}", userId, userModel.getPhone(), message);


        RepaymentFlowModel flowModel = null;
        if (finish) {
            flowModel = saveRepaymentFlowModel(ioModel, orderModel, userModel);
        }
        pushRepaymentFinish(orderModel, userModel, ioModel);

        statisticsForFinish(ioModel, orderModel, flowModel, finish,userModel);
    }


    @Transactional
    @Override
    public boolean updateOrderStatusForRepayment(OrderModel orderModel) {

        Date repaymentTime = new Date();

        OrderModel update = new OrderModel();

        update.setId(orderModel.getId());

        boolean finish = false;
        if (orderModel.getPayment() - orderModel.getAlreadyMoney() <= 0) {
            // 如果应还金额小于0 ，那么用户就是直接还完了
            int payStatus = checkDate(repaymentTime, orderModel.getRepaymentDate());

            if (orderModel.getChaseActorId() != null) {
                payStatus = Status.CHASE_LOAN.key;
            }
            update.setStatus(payStatus);
            update.setRepaymentTime(repaymentTime);

            finish = true;
        } else { // 没有还完的情况
            int compare = DateUtils.truncatedCompareTo(repaymentTime, orderModel.getRepaymentDate(), Calendar.DAY_OF_MONTH);
            update.setStatus(compare <= 0 ? Status.WAIT.key : Status.OVERDUE.key);
//            update.setRepaymentTime(repaymentTime);
        }

        orderDao.updateById(update);

        orderModel.setStatus(update.getStatus());
        orderModel.setRepaymentTime(update.getRepaymentTime());
        return finish;
    }

    @Override
    public void payOrderForDeductFail(FinanceIOModel ioModel) {

        Integer userId = ioModel.getUserId();
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), userId);


        updateOrderStatusForRepayment(orderModel);
    }


    public int checkDate(Date now, Date repaymentDate) {
        // 判断正常还是逾期还是提前
        boolean sameDay = DateUtils.isSameDay(now, repaymentDate);
        if (sameDay) {
            return Status.SUCCESS.key; //正常 2
        } else if (now.before(repaymentDate)) {
            return Status.ADVANCE_LOAN.key; //提前 6
        } else {
            return Status.OVERDUE_LOAN.key; //逾期 5
        }
    }


    private void statisticsForFinish(FinanceIOModel ioModel, OrderModel orderModel, RepaymentFlowModel flowModel, boolean finish,UserModel userModel) {
        // 还款统计埋点
        RepayStatisticDTO dto = new RepayStatisticDTO();
        dto.setRepayDate(Date8Util.date2LocalDate(orderModel.getRepaymentDate()));
        dto.setAgencyId(orderModel.getAgencyId());
        dto.setRepayMoney(ioModel.getMoney());
        dto.setVestId(userModel.getVestId());
        if (finish && flowModel != null) {
            dto.setIsFinish(CommonConst.YES);
            if (flowModel.getRepayStatus() == SUCCESS.key) {
                dto.setIsNormal(CommonConst.YES);
            } else if (flowModel.getRepayStatus() == ADVANCE_LOAN.key) {
                dto.setIsEarly(CommonConst.YES);
            } else {
                dto.setIsOverdue(CommonConst.YES);
                // 计算回收
                LocalDate realRepayDate = Date8Util.date2LocalDate(new Date());// 实还日期
                LocalDate repayDate = Date8Util.date2LocalDate(orderModel.getRepaymentDate());// 应还日期

                int days = Period.between(repayDate, realRepayDate).getDays();
                log.info("订单={},实际还款日期与应还日期相差={}天", orderModel.getOrderNumber(), days);
                if (days <= 1) {
                    dto.setIsOneOverdue(CommonConst.YES);
                } else if (days <= 3) {
                    dto.setIsThreeOverdue(CommonConst.YES);
                } else if (days <= 5) {
                    dto.setIsFifteenOverdue(CommonConst.YES);
                } else if (days <= 7) {
                    dto.setIsSevenOverdue(CommonConst.YES);
                } else {
                    dto.setIsFifteenOverdue(CommonConst.YES);
                }
            }
        }
        jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
        log.info(">>>>发送统计,线上还款jms ,data={}", dto);

        //逾期还款统计jms
        if (orderModel.getStatus() == CHASE_LOAN.key || orderModel.getStatus() == OVERDUE_LOAN.key)
            statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), OrderConstant.Stage.REPAYMENT.key, orderModel.getStatus(), userModel.getChannelCode());
    }

    private void pushRepaymentFinish(OrderModel orderModel, UserModel userModel, FinanceIOModel ioModel) {
        //推送还款成功的消息
        if (StringUtils.isBlank(userModel.getGexing())) {
            log.info("》》》推送【自动扣款】消息失败，用户【{}】找不到个推id《《《", orderModel.getUserId());
        } else {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("money", FormatUtil.formatDouble2(ioModel.getMoney()));
            PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.AUTH_DEDUCT, jsonObject);
            pushModel.setClassify(PushEnum.Classify.SINGLE.value);
            pushModel.setDataId(orderModel.getOrderNumber());
            pushModel.setUserId(orderModel.getUserId());
            pushModel.setVestId(userModel.getVestId());
            pushModel.setDeviceId(userModel.getGexing());
            jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);
            log.info("》》》推送【自动扣款】消息给用户【{}】,内容={}", orderModel.getUserId(), pushModel.getMsg());
        }
    }

    private RepaymentFlowModel saveRepaymentFlowModel(FinanceIOModel ioModel, OrderModel orderModel, UserModel userModel) {
        UserBankCardModel bankCardModel = bankCardDao.getBankCardByCardNum(ioModel.getBankCardNum(), FinanceConstant.BankCardStatus.BIND.key, ioModel.getAgencyId());
        // 插入还款流水表
        RepaymentFlowModel flowModel = new RepaymentFlowModel();
        flowModel.setOrderNumber(ioModel.getOrderId());
        flowModel.setRepaymentNumber(ioModel.getRequestId());
        flowModel.setAgencyId(userModel.getAgencyId());
        flowModel.setUserId(userModel.getId());
        flowModel.setUsername(userModel.getName());
        flowModel.setAutoRepayment(orderModel.getChargingMoney());
        flowModel.setPhone(bankCardModel.getBankPhone());
        flowModel.setPayment(orderModel.getPayment());
        flowModel.setMoney(orderModel.getAlreadyMoney());
        flowModel.setObtain(orderModel.getObtain());
        flowModel.setExceedDays(orderModel.getExceedDays());
        flowModel.setExceedFee(orderModel.getExceedFee());
        flowModel.setLoan(orderModel.getLoan());
        flowModel.setDeductMoney(orderModel.getDeductMoney());
        flowModel.setType(FinanceConstant.FlowType.DEDUCT.type);//3 自动扣款
        flowModel.setRepayType(OrderConstant.RepayType.BANKCARD.value);
        flowModel.setRepayStatus(orderModel.getStatus());
        flowModel.setPayChannel(FinanceConstant.PayPlatform.getName(ioModel.getPayPlatform()));
        flowModel.setBankName(bankCardModel.getBankName());
        flowModel.setBankNumber(bankCardModel.getBankCardNum());
        flowModel.setRepaymentDate(orderModel.getRepaymentDate());
        flowModel.setRepaymentTime(ioModel.getCreateTime());
        flowModel.setPaymentTime(orderModel.getTransferTime());
        flowModel.setRemark("");
        log.info("用户【{}】的订单【{}】还款【{}】成功，插入还款流水表成功", orderModel.getUserId(), orderModel.getOrderNumber(), ioModel.getMoney());
        repaymentFlowDao.insert(flowModel);
        return flowModel;
    }


}
