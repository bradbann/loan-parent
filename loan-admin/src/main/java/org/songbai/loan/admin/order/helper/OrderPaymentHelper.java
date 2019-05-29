package org.songbai.loan.admin.order.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.dao.RepaymentFlowDao;
import org.songbai.loan.admin.order.po.RepayPO;
import org.songbai.loan.admin.user.dao.UserDao;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.loan.RepaymentFlowModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.songbai.loan.constant.user.OrderConstant.Status.*;

/**
 * Author: qmw
 * Date: 2018/11/5 8:42 PM
 */
@Component
public class OrderPaymentHelper {
    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentHelper.class);

    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private RepaymentFlowDao repaymentFlowDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComPushTemplateService pushTemplateService;
    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private StatisSendHelper statisSendHelper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");


    public void rejectPayOrder(Integer agencyId, Integer actorId, String remark, Date againDate, OrderModel orderModel) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                OrderModel dbModel = orderDao.selectById(orderModel.getId());
                if (dbModel.getStage() != OrderConstant.Stage.LOAN.key) {
                    if (logger.isInfoEnabled()) {
                        logger.info("订单id={},不是放款阶段={},操作状态错误", dbModel.getId(), dbModel.getStage());
                    }
                    throw new RuntimeException("订单id=" + dbModel.getId() + "不是放款状态={}");
                }
                List<Integer> payStatus = Arrays.asList(1, 8);
                if (!payStatus.contains(dbModel.getStatus())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("订单id={},不是放款状态={},操作状态错误", dbModel.getId(), dbModel.getStatus());
                    }

                    throw new RuntimeException("订单id=" + dbModel.getId() + "不是放款状态={}");
                }

                OrderModel update = new OrderModel();
                update.setId(orderModel.getId());
                if (StringUtil.isNotEmpty(remark)) {
                    update.setRemark(remark);
                }
                update.setAgainDate(againDate);
                update.setStatus(OrderConstant.Status.FAIL.key);

                orderDao.updateOrderPayment(update);

                // 插入操作记录
                OrderOptModel optModel = new OrderOptModel();
                optModel.setStage(OrderConstant.Stage.LOAN.key);
                optModel.setStageFlag(OrderConstant.Stage.LOAN.name);
                optModel.setStatus(OrderConstant.Status.FAIL.key);
                optModel.setType(CommonConst.OK);
                optModel.setOrderNumber(orderModel.getOrderNumber());
                optModel.setAgencyId(agencyId);
                optModel.setActorId(actorId);
                optModel.setUserId(orderModel.getUserId());
                optModel.setRemark("原因：" + remark + "，请于" + sdf.format(againDate) + "再申请借款");
                //optModel.setRemark(remark);
                optModel.setOrderTime(orderModel.getCreateTime());
                orderOptDao.insert(optModel);

                UserModel model = comUserService.selectUserModelById(orderModel.getUserId());
                if (model == null) {
                    logger.info("推送>>>放款拒绝,用户id={},不存在", orderModel.getUserId());
                } else {

                    PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.PAY_REJECT);
                    pushModel.setClassify(PushEnum.Classify.SINGLE.value);
                    pushModel.setDataId(orderModel.getOrderNumber());
                    pushModel.setUserId(orderModel.getUserId());
                    pushModel.setVestId(model.getVestId());
                    pushModel.setDeviceId(model.getGexing());
                    jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);
                }
                transactionManager.commit(status);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("放款程序异常,用户id=," + orderModel.getUserId() + ",订单号" + orderModel.getOrderNumber(), e);
                }
                transactionManager.rollback(status);
                throw e;
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void repayConfirm(RepayPO po, OrderModel orderModel) {

        OrderModel update = new OrderModel();
        update.setId(orderModel.getId());
        update.setRepaymentTime(po.getRepaymentTime());
        update.setAlreadyMoney(po.getPayment());
        // 还款时间
        Date repaymentDate = Date8Util.LocalDate2Date(Date8Util.date2LocalDate(po.getRepaymentTime()));

        int repayStatus = SUCCESS.key;//正常还款
        if (repaymentDate.before(orderModel.getRepaymentDate())) {
            repayStatus = ADVANCE_LOAN.key;//提前还款
        } else if (repaymentDate.after(orderModel.getRepaymentDate())) {

            if (StringUtil.isNotEmpty(orderModel.getChaseId())) {
                repayStatus = CHASE_LOAN.key;//催收还款
            } else {
                repayStatus = OVERDUE_LOAN.key;//逾期还款
            }
        }
        update.setStatus(repayStatus);
        orderDao.updateById(update);

        // 插入还款记录
        RepaymentFlowModel flowModel = new RepaymentFlowModel();

        flowModel.setRepaymentDate(orderModel.getRepaymentDate());
        flowModel.setPayment(orderModel.getPayment());
        flowModel.setOrderNumber(orderModel.getOrderNumber());
        flowModel.setRepaymentNumber(OrderIdUtil.getRepaymentId());
        flowModel.setAgencyId(po.getAgencyId());
        flowModel.setUserId(orderModel.getUserId());
        flowModel.setActorId(po.getActorId());

        UserInfoModel userInfo = comUserService.findUserInfoByUserId(orderModel.getUserId());
        UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());
        //更新user表  为老客
        userModel.setGuest(OrderConstant.Guest.OLD_GUEST.key);
        userDao.updateById(userModel);
        redis.opsForHash().delete(UserRedisKey.USER_INFO, userModel.getId());

        flowModel.setUsername(userInfo.getName());
        flowModel.setPhone(userModel.getPhone());

        flowModel.setRepayType(po.getRepayType());
        flowModel.setRepayStatus(repayStatus);
        flowModel.setMoney(po.getPayment());
//        flowModel.setMoney(orderModel.getAlreadyMoney());
        flowModel.setType(FinanceConstant.FlowType.OFFLINE.type);
        flowModel.setReceipt(po.getReceipt());
        flowModel.setRemark(po.getRemark());
        flowModel.setRepaymentTime(po.getRepaymentTime());
        flowModel.setPaymentTime(orderModel.getTransferTime());
        flowModel.setDeductMoney(orderModel.getDeductMoney());
        flowModel.setExceedDays(orderModel.getExceedDays());
        flowModel.setAutoRepayment(orderModel.getChargingMoney());
        flowModel.setExceedFee(orderModel.getExceedFee());
        flowModel.setLoan(orderModel.getLoan());
        flowModel.setObtain(orderModel.getObtain());


        repaymentFlowDao.insert(flowModel);

        // 插入操作记录
        OrderOptModel optModel = new OrderOptModel();
        optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
        optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
        optModel.setStatus(repayStatus);
        optModel.setType(CommonConst.OK);
        optModel.setOrderNumber(orderModel.getOrderNumber());
        optModel.setAgencyId(po.getAgencyId());
        optModel.setActorId(po.getActorId());
        optModel.setUserId(orderModel.getUserId());
        optModel.setGuest(orderModel.getGuest());
        optModel.setRemark(po.getRemark());
        optModel.setOrderTime(orderModel.getCreateTime());
        orderOptDao.insert(optModel);


        sendRepayJms(po, orderModel, repayStatus);
        //信审逾期统计jms
        if (repayStatus == CHASE_LOAN.key || repayStatus == OVERDUE_LOAN.key) {
            statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), OrderConstant.Stage.REPAYMENT.key, repayStatus, userModel.getChannelCode());
        }

    }

    public void deductComplate(Double deductMoney, Integer actorId, OrderModel orderModel) {
        Date current = new Date();
        OrderModel update = new OrderModel();
        update.setId(orderModel.getId());
        update.setRepaymentTime(current);
        update.setPayment(orderModel.getPayment() - deductMoney);
        update.setDeductMoney(orderModel.getDeductMoney() + deductMoney);
        // 还款时间
        Date repaymentDate = Date8Util.LocalDate2Date(Date8Util.date2LocalDate(current));

        int repayStatus = SUCCESS.key;//正常还款
        if (repaymentDate.before(orderModel.getRepaymentDate())) {
            repayStatus = ADVANCE_LOAN.key;//提前还款
        } else if (repaymentDate.after(orderModel.getRepaymentDate())) {

            if (StringUtil.isNotEmpty(orderModel.getChaseId())) {
                repayStatus = CHASE_LOAN.key;//催收还款
            } else {
                repayStatus = OVERDUE_LOAN.key;//逾期还款
            }
        }
        update.setStatus(repayStatus);
        orderDao.updateById(update);

        // 插入还款记录
        RepaymentFlowModel flowModel = new RepaymentFlowModel();

        flowModel.setRepaymentDate(orderModel.getRepaymentDate());

        flowModel.setOrderNumber(orderModel.getOrderNumber());
        flowModel.setAutoRepayment(orderModel.getChargingMoney());
        flowModel.setRepaymentNumber(OrderIdUtil.getRepaymentId());
        flowModel.setAgencyId(orderModel.getAgencyId());
        flowModel.setUserId(orderModel.getUserId());
        flowModel.setActorId(actorId);

        UserInfoModel userInfo = comUserService.findUserInfoByUserId(orderModel.getUserId());
        UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());
        if (userModel.getGuest() != OrderConstant.Guest.OLD_GUEST.key) {
            //更新user表  为老客
            userModel.setGuest(OrderConstant.Guest.OLD_GUEST.key);
            userDao.updateById(userModel);
            redis.opsForHash().delete(UserRedisKey.USER_INFO, userModel.getId());
        }

        flowModel.setUsername(userInfo.getName());
        flowModel.setPhone(userModel.getPhone());

        flowModel.setRepayType(OrderConstant.RepayType.BANKCARD.value);
        flowModel.setRepayStatus(repayStatus);
//        flowModel.setMoney(update.getPayment());
        flowModel.setMoney(orderModel.getAlreadyMoney());
        flowModel.setPayment(update.getPayment());
        flowModel.setType(FinanceConstant.FlowType.OFFLINE.type);
        flowModel.setRemark("该订单被减免结清");
        flowModel.setRepaymentTime(current);
        flowModel.setPaymentTime(orderModel.getTransferTime());
        flowModel.setDeductMoney(orderModel.getDeductMoney());
        flowModel.setExceedDays(orderModel.getExceedDays());
        flowModel.setExceedFee(orderModel.getExceedFee());
        flowModel.setLoan(orderModel.getLoan());
        flowModel.setObtain(orderModel.getObtain());

        repaymentFlowDao.insert(flowModel);

        // 插入操作记录
        OrderOptModel optModel = new OrderOptModel();
        optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
        optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
        optModel.setStatus(OrderConstant.Status.DEDUCT.key);
        optModel.setType(CommonConst.OK);
        optModel.setOrderNumber(orderModel.getOrderNumber());
        optModel.setAgencyId(orderModel.getAgencyId());
        optModel.setActorId(actorId);
        optModel.setUserId(orderModel.getUserId());
        optModel.setGuest(orderModel.getGuest());
        optModel.setRemark("减免结清");
        optModel.setOrderTime(orderModel.getCreateTime());
        orderOptDao.insert(optModel);

        RepayPO po = new RepayPO();
        po.setPayment(deductMoney);
        po.setRepaymentTime(current);

        sendRepayJms(po, orderModel, repayStatus);
        //信审逾期统计jms
        //信审逾期统计jms
        if (repayStatus == CHASE_LOAN.key || repayStatus == OVERDUE_LOAN.key) {
            statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), OrderConstant.Stage.REPAYMENT.key, repayStatus, userModel.getChannelCode());
        }

    }

    private void sendRepayJms(RepayPO po, OrderModel orderModel, int repayStatus) {
        RepayStatisticDTO dto = new RepayStatisticDTO();
        dto.setRepayDate(Date8Util.date2LocalDate(orderModel.getRepaymentDate()));
        dto.setAgencyId(orderModel.getAgencyId());
        dto.setDeductMoney(po.getPayment());
        dto.setIsFinish(CommonConst.YES);

        UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());

        dto.setVestId(userModel.getVestId());

        if (repayStatus == SUCCESS.key) {
            dto.setIsNormal(CommonConst.YES);
        } else if (repayStatus == ADVANCE_LOAN.key) {
            dto.setIsEarly(CommonConst.YES);
        } else {
            dto.setIsOverdue(CommonConst.YES);
            // 计算回收
            LocalDate repayDate = Date8Util.date2LocalDate(orderModel.getRepaymentDate());// 应还日期
            LocalDate realRepayDate = Date8Util.date2LocalDate(po.getRepaymentTime());// 实还日期

            int days = Period.between(repayDate, realRepayDate).getDays();
            logger.info("订单={},实际还款日期与应还日期相差={}天", orderModel.getOrderNumber(), days);
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
        jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
        logger.info(">>>>发送统计,线下还款jms ,data={}", dto);
    }

}
