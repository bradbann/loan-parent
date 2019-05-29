package org.songbai.loan.user.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.helper.TransactionHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.user.DeductConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.*;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.dao.ComUserDao;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.dao.RepaymentFlowDao;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.FinanceDeductService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.songbai.loan.constant.user.OrderConstant.Status.*;

/**
 * 【线上还款的】公共的资金操作表的方法
 *
 * @author wjl
 * @date 2018年11月12日 16:41:05
 * @description
 */
@Service
public class BasicOrderServiceImpl implements BasicOrderService {
    private static final Logger log = LoggerFactory.getLogger(BasicOrderServiceImpl.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ComUserDao comUserDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RepaymentFlowDao repaymentFlowDao;
    @Autowired
    private UserBankCardDao bankCardDao;
    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private ComPushTemplateService pushTemplateService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private FinanceIODao ioDao;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private FinanceDeductService financeDeductService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    StatisSendHelper statisSendHelper;

    /**
     * 只有成功才会创建订单 减少垃圾订单
     */
    @Override
    @Transactional
    public FinanceIOModel initOrder(UserModel user, OrderModel orderModel, String requestId, String bankCardNum, String payPlatform, Integer payType) {
        //先插入操作记录表
        OrderOptModel optModel = new OrderOptModel();
        optModel.setOrderNumber(orderModel.getOrderNumber());
        optModel.setAgencyId(orderModel.getAgencyId());
        optModel.setUserId(orderModel.getUserId());
        optModel.setGuest(user.getGuest());
        optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
        optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
        optModel.setStatus(OrderConstant.Status.PROCESSING.key);
        optModel.setRemark("开始还款 金额:" + (Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney())));
        optModel.setOrderTime(orderModel.getCreateTime());
        orderOptDao.insert(optModel);

        FinanceIOModel ioModel = new FinanceIOModel();
        ioModel.setUserId(user.getId());
        ioModel.setAgencyId(user.getAgencyId());
        ioModel.setThirdUserId(user.getThirdId());
        ioModel.setOrderId(orderModel.getOrderNumber());
        ioModel.setRequestId(requestId);
        if (StringUtils.isNotBlank(payPlatform))
            ioModel.setPayPlatform(payPlatform);
        ioModel.setBankCardNum(bankCardNum);
        ioModel.setType(FinanceConstant.PayType.REPAY.type);
        ioModel.setTypeDetail(FinanceConstant.PayType.REPAY.typeDetail);
        ioModel.setMoney(Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney()));
        ioModel.setStatus(FinanceConstant.IoStatus.PROCESSING.key);
        ioModel.setPayType(payType);//OrderConstant.RepayType
        ioDao.insert(ioModel);
        return ioModel;
    }

    @Override
    public FinanceIOModel initOrder(PayOrderVO orderVO, PayBankCardVO bankCardVO, String requestId, String payPlatform, FinanceConstant.PayType payType) {

        FinanceIOModel ioModel = new FinanceIOModel();
        ioModel.setUserId(bankCardVO.getUserId());
        ioModel.setAgencyId(orderVO.getAgencyId());
        ioModel.setThirdUserId(bankCardVO.getUserThridId());
        ioModel.setOrderId(orderVO.getOrderNumber());
        ioModel.setRequestId(requestId);
        ioModel.setPayPlatform(payPlatform);
        ioModel.setBankCardNum(bankCardVO.getBankCardNum());
        ioModel.setType(payType.type);
        ioModel.setTypeDetail(payType.typeDetail);
        ioModel.setMoney(orderVO.getPayment());
        ioModel.setStatus(FinanceConstant.IoStatus.PROCESSING.key);
        ioModel.setPayType(OrderConstant.RepayType.BANKCARD.key);//2 网上银行支付 第三方支付
        ioDao.insert(ioModel);
        return ioModel;
    }


    @Override
    public void updateIoStatus(FinanceIOModel ioModel, Integer status) {
        FinanceIOModel updateIoModel = new FinanceIOModel();
        updateIoModel.setId(ioModel.getId());
//		updateIoModel.setThirdOrderId(ioModel.getThirdOrderId());
        updateIoModel.setStatus(status);//1
        ioDao.updateById(updateIoModel);
    }


    @Override
    @Transactional
    public void repaymentSuccess(FinanceIOModel ioModel) {
        // 订单交易成功
        // 先更新io表
        FinanceIOModel updateIoModel = new FinanceIOModel();
        updateIoModel.setId(ioModel.getId());
        updateIoModel.setThirdOrderId(ioModel.getThirdOrderId());
        updateIoModel.setStatus(FinanceConstant.IoStatus.SUCCESS.key);//1
        ioDao.updateById(updateIoModel);
        // 更新order表状态
        Integer userId = ioModel.getUserId();
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), userId);
        OrderModel updateOrderModel = new OrderModel();
        updateOrderModel.setId(orderModel.getId());
        updateOrderModel.setAlreadyMoney(Arith.add(2, ioModel.getMoney(), orderModel.getAlreadyMoney()));
        Date repaymentTime = new Date();
        int payStatus = checkDate(repaymentTime, orderModel.getRepaymentDate());
        if (orderModel.getChaseActorId() != null) {
            payStatus = Status.CHASE_LOAN.key;
        }
        updateOrderModel.setStatus(payStatus);
        updateOrderModel.setRepaymentTime(repaymentTime);
        orderDao.updateById(updateOrderModel);

        //更新user表
        UserModel userModel = comUserDao.selectUserModelById(userId);
        if (userModel.getGuest() == OrderConstant.Guest.NEW_GUEST.key) {
            UserModel updateUserModel = new UserModel();
            updateUserModel.setId(userModel.getId());
            updateUserModel.setGuest(OrderConstant.Guest.OLD_GUEST.key);
            userDao.updateById(updateUserModel);
            redis.opsForHash().delete(UserRedisKey.USER_INFO, userModel.getId());
        }

        //更新opt表
        OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(orderModel.getOrderNumber(), orderModel.getUserId());
        OrderOptModel updateOrderOptModel = new OrderOptModel();
        updateOrderOptModel.setId(lastUpdateOpt.getId());
        updateOrderOptModel.setStatus(payStatus);
        updateOrderOptModel.setRemark("还款成功");
        updateOrderOptModel.setGuest(userModel.getGuest());
        orderOptDao.updateById(updateOrderOptModel);


        // 插入还款流水表
        RepaymentFlowModel flowModel = new RepaymentFlowModel();
        if (StringUtils.isNotBlank(ioModel.getBankCardNum())) {
            UserBankCardModel bankCardModel = bankCardDao.getBankCardByCardNum(ioModel.getBankCardNum(), FinanceConstant.BankCardStatus.BIND.key, ioModel.getAgencyId());
//            flowModel.setPhone(bankCardModel.getBankPhone());
            flowModel.setBankName(bankCardModel.getBankName());
            flowModel.setBankNumber(bankCardModel.getBankCardNum());
        }
        flowModel.setPhone(userModel.getPhone());
        flowModel.setOrderNumber(ioModel.getOrderId());
        flowModel.setRepaymentNumber(ioModel.getRequestId());
        flowModel.setAutoRepayment(orderModel.getChargingMoney());
        flowModel.setAgencyId(userModel.getAgencyId());
        flowModel.setUserId(userId);
        flowModel.setUsername(userModel.getName());
        flowModel.setMoney(updateOrderModel.getAlreadyMoney());
        flowModel.setPayment(orderModel.getPayment());
        flowModel.setObtain(orderModel.getObtain());
        flowModel.setExceedDays(orderModel.getExceedDays());
        flowModel.setExceedFee(orderModel.getExceedFee());
        flowModel.setLoan(orderModel.getLoan());
        flowModel.setDeductMoney(orderModel.getDeductMoney());
        if (ioModel.getOperatorId() != null) {//自动扣款   1线上还款,2线下还款,3自动扣款
            flowModel.setActorId(ioModel.getOperatorId());
            flowModel.setType(FinanceConstant.FlowType.OFFLINE.type);//3 自动扣款
        } else {
            flowModel.setType(FinanceConstant.FlowType.ONLINE.type);//1 线上还款
        }
        flowModel.setRepayType(OrderConstant.RepayType.parseName(ioModel.getPayType()).value);
        flowModel.setRepayStatus(payStatus);
        flowModel.setPayChannel(FinanceConstant.PayPlatform.getName(ioModel.getPayPlatform()));

        flowModel.setRepaymentDate(orderModel.getRepaymentDate());
        flowModel.setRepaymentTime(repaymentTime);
        flowModel.setPaymentTime(orderModel.getTransferTime());
        log.info("用户【{}】的订单【{}】还款【{}】成功，插入还款流水表成功", orderModel.getUserId(), orderModel.getOrderNumber(), ioModel.getMoney());
        repaymentFlowDao.insert(flowModel);
        redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);


        //推送还款成功的消息
        if (StringUtils.isBlank(userModel.getGexing())) {
            log.info("》》》推送【还款成功】消息失败，用户【{}】找不到个推id《《《", orderModel.getUserId());
        } else {
            log.info("》》》推送【还款成功】消息给用户【{}】成功《《《", orderModel.getUserId());
            PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.REPAY_SUCCESS);
            pushModel.setClassify(PushEnum.Classify.SINGLE.value);
            pushModel.setDataId(orderModel.getOrderNumber());
            pushModel.setUserId(orderModel.getUserId());
            pushModel.setVestId(userModel.getVestId());
            pushModel.setDeviceId(userModel.getGexing());
            jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);
        }

        sendJmsStatis(userModel,orderModel,ioModel,flowModel,repaymentTime,payStatus,CommonConst.YES);
    }

    private void sendJmsStatis(UserModel userModel, OrderModel orderModel, FinanceIOModel ioModel, RepaymentFlowModel flowModel, Date repaymentTime, int payStatus,Integer isFinish) {

        // 还款统计埋点
        RepayStatisticDTO dto = new RepayStatisticDTO();
        dto.setRepayDate(Date8Util.date2LocalDate(orderModel.getRepaymentDate()));
        dto.setAgencyId(orderModel.getAgencyId());
        dto.setRepayMoney(ioModel.getMoney());
        dto.setIsFinish(isFinish);

        if (flowModel.getRepayStatus() == SUCCESS.key) {
            dto.setIsNormal(CommonConst.YES);
        } else if (flowModel.getRepayStatus() == ADVANCE_LOAN.key) {
            dto.setIsEarly(CommonConst.YES);
        } else {
            dto.setIsOverdue(CommonConst.YES);
            // 计算回收
            LocalDate realRepayDate = Date8Util.date2LocalDate(repaymentTime);// 实还日期
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
        dto.setVestId(userModel.getVestId());
        jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
        log.info(">>>>发送统计,线上还款jms ,data={}", dto);

        //逾期还款统计jms
        if (payStatus == CHASE_LOAN.key || payStatus == OVERDUE_LOAN.key) {
            statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), OrderConstant.Stage.REPAYMENT.key, payStatus, userModel.getChannelCode());
        }
    }

    @Override
    @Transactional
    public void dealOrderFailed(FinanceIOModel ioModel, String errorMsg, Boolean confirm) {
        //如果confirm 为true 就说明走过确认接口
        if (confirm) {
            OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
            OrderModel updateOrderModel = new OrderModel();
            updateOrderModel.setId(orderModel.getId());
            int status = checkDate(new Date(), orderModel.getRepaymentDate());
            if (status == Status.OVERDUE_LOAN.key) {
                updateOrderModel.setStatus(Status.OVERDUE.key);
            } else {
                updateOrderModel.setStatus(Status.WAIT.key);//待还款的状态
            }
            orderDao.updateById(updateOrderModel);
            log.info("用户【{}】的订单【{}】还款受理失败，更改为【{}】状态", ioModel.getUserId(), orderModel.getOrderNumber(), status == Status.OVERDUE_LOAN.key ? "逾期" : "待还款");

            //逾期订单再次进行逾期统计
            if (status == Status.OVERDUE_LOAN.key){
                jmsTemplate.convertAndSend(new JSONObject());
            }
        }
        //更改opt表
        OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(ioModel.getOrderId(), ioModel.getUserId());
        OrderOptModel updateOrderOptModel = new OrderOptModel();
        updateOrderOptModel.setId(lastUpdateOpt.getId());
        updateOrderOptModel.setStatus(Status.EXCEPTION.key);
        updateOrderOptModel.setRemark("还款失败:" + errorMsg);
        orderOptDao.updateById(updateOrderOptModel);

        FinanceIOModel updateIoModel = new FinanceIOModel();
        updateIoModel.setId(ioModel.getId());
        updateIoModel.setStatus(FinanceConstant.IoStatus.FAILED.key);
        updateIoModel.setRemark(errorMsg);
        ioDao.updateById(updateIoModel);
        redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, ioModel.getUserId());

    }

    @Override
    public void dealOrderSuccess(FinanceIOModel ioModel) {
        //更新order为进行中
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        OrderModel updateOrderModel = new OrderModel();
        updateOrderModel.setId(orderModel.getId());
        updateOrderModel.setStatus(OrderConstant.Status.PROCESSING.key);
        log.info("user:{} request changJie pay order:{} repayment success, modify repayment processing", ioModel.getUserId(), orderModel.getOrderNumber());
        orderDao.updateById(updateOrderModel);
    }


    //-----------------------------------------------------以下是代扣的处理逻辑，本想合一块但是还是要封装的----------------------------------------------------------
//	@Override
//	@Transactional
//	public void initAutoRepayOrder(UserModel user, OrderModel orderModel, String requestId, UserBankCardModel bankCardModel, String payPlatform, Integer actorId, Double money) {
    //先插入操作记录表
//		OrderOptModel optModel = new OrderOptModel();
//		optModel.setOrderNumber(orderModel.getOrderNumber());
//		optModel.setAgencyId(orderModel.getAgencyId());
//		optModel.setUserId(orderModel.getUserId());
//		optModel.setGuest(user.getGuest());
//		optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
//		optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
//		optModel.setStatus(OrderConstant.Status.PROCESSING.key);
//		optModel.setActorId(actorId);
//		optModel.setRemark("开始代扣");
//		optModel.setOrderTime(orderModel.getCreateTime());
//		orderOptDao.insert(optModel);
//
//		//在插入所有交易记录表
//		FinanceIOModel ioModel = new FinanceIOModel();
//		ioModel.setUserId(user.getId());
//		ioModel.setAgencyId(user.getAgencyId());
//		ioModel.setThirdUserId(user.getThirdId());
//		ioModel.setOrderId(orderModel.getOrderNumber());
//		ioModel.setRequestId(requestId);
//		ioModel.setPayPlatform(payPlatform);
//		ioModel.setBankCardNum(bankCardModel.getBankCardNum());
//		ioModel.setOperatorId(actorId);
//		ioModel.setType(FinanceConstant.PayType.AUTOREPAY.type);
//		ioModel.setTypeDetail(FinanceConstant.PayType.AUTOREPAY.typeDetail);
//		ioModel.setMoney(money);
//		ioModel.setStatus(FinanceConstant.IoStatus.PROCESSING.key);
//		ioModel.setPayType(OrderConstant.RepayType.BANKCARD.key);//2 网上银行支付 第三方支付
//		ioDao.insert(ioModel);
//
//		//最后插入代扣流水表
//		RepaymentAutoFlowModel autoFlowModel = new RepaymentAutoFlowModel();
//		autoFlowModel.setUserId(user.getId());
//		autoFlowModel.setAgencyId(user.getAgencyId());
//		autoFlowModel.setActorId(actorId);
//		autoFlowModel.setOrderNumber(orderModel.getOrderNumber());
//		autoFlowModel.setAutoRepaymentNumber(requestId);
//		autoFlowModel.setName(user.getName());
//		autoFlowModel.setPhone(user.getPhone());
//		autoFlowModel.setChannelId(user.getChannelId());
//		//  先不写，马甲写完在完善 setVestId()
//		autoFlowModel.setPayment(orderModel.getPayment());
//		autoFlowModel.setAutoRePayment(money);
//		autoFlowModel.setStatus(FinanceConstant.IoStatus.INIT.key);
//		autoFlowModel.setPayPlatform(payPlatform);
//		autoFlowModel.setBankName(bankCardModel.getBankName());
//		autoFlowModel.setBankCardNum(bankCardModel.getBankCardNum());
//		autoFlowModel.setRepaymentDate(orderModel.getRepaymentDate());
//		autoFlowModel.setAutoRepaymentTime(new Date());
//		repayAutoFlowDao.insert(autoFlowModel);
//	}

    @Override
    public void deductSuccess(FinanceIOModel ioModel) {

        Map<String, Object> recursionMap = new HashMap<>();

        transactionHelper.tx(() -> {
            updateIoStatus(ioModel, FinanceConstant.IoStatus.SUCCESS.key);
            // deductModel 表里面
            FinanceDeductFlowModel flowModel = financeDeductService.updateDeductFlowStatusForIoModel(ioModel, true, "success");

            // 用户订单 金额处理
            orderPayService.payOrderForDeduct(ioModel);

            FinanceDeductModel deductModel = financeDeductService.updateDeductMoney(flowModel.getDeductId(), ioModel.getMoney());
            boolean deductFinish = deductModel.getDeductMoney() >= deductModel.getPayment();

            if (deductFinish) {
                financeDeductService.updateDeductStatus(deductModel, DeductConst.Status.FINISH.code, "支付成功, 扣完结束");
            } else {
                double[] level = financeDeductService.getDeductLimit(deductModel, flowModel);
                // 表示还需要继续扣款。
                if (level[1] > 0) {
                    financeDeductService.updateDeductStatus(deductModel, DeductConst.Status.WAIT.code, "支付成功，等待下一次扣款");
                    recursionMap.put("deductId", deductModel.getId());
                    recursionMap.put("orderNumber", deductModel.getOrderNumber());
                } else {
                    financeDeductService.updateDeductStatus(deductModel, DeductConst.Status.FINISH.code, "支付成功，不进行下一次扣款");
                }
            }
        });

        if (recursionMap.size() > 1) {
            jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, JSON.toJSONString(recursionMap));
        }

    }

    @Override
    public void deductFailed(FinanceIOModel ioModel, String errorMsg) {

        Map<String, Object> recursionMap = new HashMap<>();

        transactionHelper.tx(() -> {
            //代扣成功逻辑，需要更改order表的应还金额
            updateIoStatus(ioModel, FinanceConstant.IoStatus.FAILED.key);

            orderPayService.payOrderForDeductFail(ioModel);

            // deductModel 表里面

            FinanceDeductFlowModel flowModel = financeDeductService.updateDeductFlowStatusForIoModel(ioModel, false, errorMsg + ", 自动扣款 扣款失败，扣款金额：" + ioModel.getMoney());

            FinanceDeductModel deductModel = financeDeductService.selectDeductModelById(flowModel.getDeductId());

            financeDeductService.updateDeductStatusAndNum(deductModel, DeductConst.Status.FINISH.code, "扣款结束:" + errorMsg);


            OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());

            OrderOptModel updateOrderOptModel = new OrderOptModel();
            updateOrderOptModel.setStatus(orderModel.getStatus());
            updateOrderOptModel.setRemark("自动扣款 扣款失败，扣款金额：" + ioModel.getMoney());
            updateOrderOptModel.setGuest(orderModel.getGuest());
            updateOrderOptModel.setUserId(orderModel.getUserId());
            updateOrderOptModel.setAgencyId(orderModel.getAgencyId());
            updateOrderOptModel.setOrderNumber(orderModel.getOrderNumber());
            updateOrderOptModel.setStatus(Status.AUTO_DEDUCT.key);
            updateOrderOptModel.setStage(orderModel.getStage());
            updateOrderOptModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);

            orderOptDao.insert(updateOrderOptModel);

            double[] level = financeDeductService.getDeductLimit(deductModel, flowModel);

            // 表示还需要继续扣款。
            if (level[1] > 0) {
                financeDeductService.updateDeductStatus(deductModel, DeductConst.Status.WAIT.code, "支付失败，等待下一次扣款");
                recursionMap.put("deductId", deductModel.getId());
                recursionMap.put("orderNumber", deductModel.getOrderNumber());
            } else {
                financeDeductService.updateDeductStatus(deductModel, DeductConst.Status.FINISH.code, "支付失败，不进行下一次扣款");
            }

        });

        if (!recursionMap.isEmpty()) {
            jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, JSON.toJSONString(recursionMap));
        }

    }

    @Override
    public void updateOrderStatus(Integer stage, Integer status, String orderNum) {
        orderDao.updateOrderStatus(stage, status, orderNum);
    }

    @Override
    public void repaymentUpdateIo(FinanceIOModel ioModel) {
        // 先更新io表
        FinanceIOModel updateIoModel = new FinanceIOModel();
        updateIoModel.setId(ioModel.getId());
        updateIoModel.setThirdOrderId(ioModel.getThirdOrderId());
        updateIoModel.setStatus(FinanceConstant.IoStatus.SUCCESS.key);//1
        ioDao.updateById(updateIoModel);

        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        UserModel userModel = comUserDao.selectUserModelById(ioModel.getUserId());


        OrderModel updateOrderModel = new OrderModel();
        updateOrderModel.setId(orderModel.getId());
        updateOrderModel.setAlreadyMoney(Arith.add(2, ioModel.getMoney(), orderModel.getAlreadyMoney()));
        Date repaymentTime = new Date();
        int payStatus = checkDate(repaymentTime, orderModel.getRepaymentDate());
        if (orderModel.getChaseActorId() != null) {
            payStatus = Status.CHASE_LOAN.key;
        }

        int status = checkDate(new Date(), orderModel.getRepaymentDate());
        if (status == Status.OVERDUE_LOAN.key) {
            updateOrderModel.setStatus(Status.OVERDUE.key);
        } else {
            updateOrderModel.setStatus(Status.WAIT.key);//待还款的状态
        }

        updateOrderModel.setRepaymentTime(repaymentTime);
        orderDao.updateById(updateOrderModel);

        //更新opt表
        OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(ioModel.getOrderId(), ioModel.getUserId());
        OrderOptModel updateOrderOptModel = new OrderOptModel();
        updateOrderOptModel.setId(lastUpdateOpt.getId());
        updateOrderOptModel.setStatus(payStatus);
        updateOrderOptModel.setRemark("还款成功");
        updateOrderOptModel.setGuest(userModel.getGuest());
        orderOptDao.updateById(updateOrderOptModel);

        // 插入还款流水表
        RepaymentFlowModel flowModel = new RepaymentFlowModel();
        if (StringUtils.isNotBlank(ioModel.getBankCardNum())) {
            UserBankCardModel bankCardModel = bankCardDao.getBankCardByCardNum(ioModel.getBankCardNum(), FinanceConstant.BankCardStatus.BIND.key, ioModel.getAgencyId());
//            flowModel.setPhone(bankCardModel.getBankPhone());
            flowModel.setBankName(bankCardModel.getBankName());
            flowModel.setBankNumber(bankCardModel.getBankCardNum());
        }
        flowModel.setPhone(userModel.getPhone());
        flowModel.setOrderNumber(ioModel.getOrderId());
        flowModel.setRepaymentNumber(ioModel.getRequestId());
        flowModel.setAutoRepayment(orderModel.getChargingMoney());
        flowModel.setAgencyId(userModel.getAgencyId());
        flowModel.setUserId(ioModel.getUserId());
        flowModel.setUsername(userModel.getName());
        flowModel.setMoney(Arith.add(2, ioModel.getMoney(), orderModel.getAlreadyMoney()));
        flowModel.setPayment(orderModel.getPayment());
        flowModel.setObtain(orderModel.getObtain());
        flowModel.setExceedDays(orderModel.getExceedDays());
        flowModel.setExceedFee(orderModel.getExceedFee());
        flowModel.setLoan(orderModel.getLoan());
        flowModel.setDeductMoney(orderModel.getDeductMoney());
        if (ioModel.getOperatorId() != null) {//自动扣款   1线上还款,2线下还款,3自动扣款
            flowModel.setActorId(ioModel.getOperatorId());
            flowModel.setType(FinanceConstant.FlowType.OFFLINE.type);//3 自动扣款
        } else {
            flowModel.setType(FinanceConstant.FlowType.ONLINE.type);//1 线上还款
        }
        flowModel.setRepayType(OrderConstant.RepayType.parseName(ioModel.getPayType()).value);
        flowModel.setRepayStatus(payStatus);
        flowModel.setPayChannel(FinanceConstant.PayPlatform.getName(ioModel.getPayPlatform()));

        flowModel.setRepaymentDate(orderModel.getRepaymentDate());
        flowModel.setRepaymentTime(repaymentTime);
        flowModel.setPaymentTime(orderModel.getTransferTime());
        log.info("用户【{}】的订单【{}】还款【{}】成功，插入还款流水表成功", orderModel.getUserId(), orderModel.getOrderNumber(), ioModel.getMoney());
        repaymentFlowDao.insert(flowModel);


        //发送统计信息
        sendJmsStatis(userModel,orderModel,ioModel,flowModel,repaymentTime,payStatus,CommonConst.NO);
    }

    private int checkDate(Date now, Date repaymentDate) {
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
}
