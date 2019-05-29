package org.songbai.loan.admin.order.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.excel.ExcelNewHelper;
import org.songbai.cloud.basics.utils.excel.ExcelWriteBuilder;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.dao.PaymentFlowDao;
import org.songbai.loan.admin.order.dao.RepaymentFlowDao;
import org.songbai.loan.admin.order.helper.OrderPaymentHelper;
import org.songbai.loan.admin.order.po.*;
import org.songbai.loan.admin.order.service.OrderOptService;
import org.songbai.loan.admin.order.service.OrderService;
import org.songbai.loan.admin.order.vo.*;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.OrderConstant.AuthStatus;
import org.songbai.loan.constant.user.OrderConstant.Stage;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderServiceImpl implements OrderService {
    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderOptService orderOptService;
    @Autowired
    private OrderPaymentHelper paymentHelper;
    @Autowired
    private PaymentFlowDao paymentFlowDao;
    @Autowired
    private AdminActorDao adminActorDao;
    @Autowired
    private RepaymentFlowDao repaymentFlowDao;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    ComUserService comUserService;
    @Autowired
    ComPushTemplateService comPushTemplateService;
    @Autowired
    ComAgencyService comAgencyService;
    @Autowired
    ExcelNewHelper excelNewHelper;
    @Autowired
    private OrderPaymentHelper orderPaymentHelper;
    @Autowired
    StatisSendHelper statisSendHelper;


    @Override
    public Page<OrderPageVo> orderList(OrderPo orderPo) {
        if (orderPo.getStatus() != null) {
            String[] split = orderPo.getStatus().split(",");
            orderPo.setOrderStage(Integer.valueOf(split[0]));
            orderPo.setOrderStatus(Integer.valueOf(split[1]));
            if (split.length == 3) {
                orderPo.setOrderAuthStatus(Integer.valueOf(split[2]));
            }
        }
        Integer count = orderDao.queryOrderCount(orderPo);
        if (count == 0) {
            return new Page<>(orderPo.getPage(), orderPo.getPageSize(), count, new ArrayList<>());
        }
        List<OrderPageVo> list = orderDao.queryOrderPage(orderPo);
        for (OrderPageVo orderPageVo : list) {
            if (orderPageVo.getStage() == 2 && orderPageVo.getStatus() == 1) {
                orderPageVo.setOrderStatusName(orderPageVo.getStage() + "," + orderPageVo.getStatus() + "," + orderPageVo.getAuthStatus());
            } else {
                orderPageVo.setOrderStatusName(orderPageVo.getStage() + "," + orderPageVo.getStatus());
            }
            AgencyModel agencyModel = comAgencyService.findAgencyById(orderPageVo.getAgencyId());
            if (agencyModel != null) orderPageVo.setAgencyName(agencyModel.getAgencyName());
            if (orderPageVo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(orderPageVo.getVestId());
                if (vestModel != null) orderPageVo.setVestName(vestModel.getName());
            }
            if (StringUtil.isNotEmpty(orderPageVo.getChannelCode())) {
                AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(orderPageVo.getAgencyId(), orderPageVo.getChannelCode());
                if (channel != null && StringUtil.isNotEmpty(channel.getChannelName())) {
                    orderPageVo.setChannelCode(channel.getChannelName());
                }
            }
        }
        return new Page<>(orderPo.getPage(), orderPo.getPageSize(), count, list);
    }

    @Override
    public Page<OrderPageVo> getWaitReviceOrderPage(OrderPo orderPo) {

        orderPo.setOrderStage(Stage.ARTIFICIAL_AUTH.key);
        orderPo.setOrderStatus(OrderConstant.Status.WAIT.key);
//        orderPo.setOrderAuthStatus(AuthStatus.WAIT_REVIEW.key);
        Integer count = orderDao.queryOrderCount(orderPo);
        if (count == 0) {
            return new Page<>(orderPo.getPage(), orderPo.getPageSize(), count, new ArrayList<>());
        }
        List<OrderPageVo> list = orderDao.queryOrderPage(orderPo);
        List<OrderPageVo> result = new ArrayList<>();
        for (OrderPageVo vo : list) {
            OrderOptModel optModel = orderOptService.findOptLimitOne(vo.getOrderNumber(), vo.getAgencyId(), Stage.LOAN.key, Status.OVERDUE.key);
            if (optModel != null) vo.setReturnRemark(optModel.getRemark());
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
            result.add(vo.change(vo));
        }
        return new Page<>(orderPo.getPage(), orderPo.getPageSize(), count, result);
    }

    @Override
    public Integer takeOrder(Integer count, Integer agencyId, Integer actorId) {
        Date date = new Date();
        return orderDao.updateOrderActorId(agencyId, count, actorId, date);
    }

    @Override
    public List<OrderPageVo> getOwnerReviceOrder(OrderPo orderPo, Integer agencyId, Integer actorId) {
        orderPo.setOrderStage(Stage.ARTIFICIAL_AUTH.key);
        orderPo.setOrderStatus(Status.WAIT.key);
        orderPo.setOrderAuthStatus(AuthStatus.OVER_TKE.key);
        List<OrderPageVo> list = orderDao.getOwnerReviceOrder(orderPo, agencyId, actorId);
        list.forEach(e -> {
            AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
            if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) e.setVestName(vestModel.getName());
            }
        });
        return list;
    }

    @Override
    @Transactional
    public void updateOrderAuthStatus(String orderNumber, Integer agencyId, Integer actorId, Integer orderStatus, String remark) {

        if (!orderStatus.equals(Status.SUCCESS.key) && !orderStatus.equals(Status.FAIL.key)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "审核状态不在范围内");
        }

        if (orderStatus.equals(Status.FAIL.key) && StringUtils.isEmpty(remark)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "备注不能为空");
        }

        OrderModel oldOrder = orderDao.selectInfoByOrderNumb(orderNumber);
        if (oldOrder == null) {
            throw new BusinessException(AdminRespCode.ORDER_NOT_EXISIT);
        }

        if (!oldOrder.getStage().equals(Stage.ARTIFICIAL_AUTH.key)
                && !oldOrder.getStatus().equals(Status.WAIT.key)) {
            throw new BusinessException(AdminRespCode.ORDER_STATUS_IS_CHANGE);
        }

        if (!agencyId.equals(oldOrder.getAgencyId()) && agencyId != 0) {
            throw new BusinessException(AdminRespCode.ACCESS_ADMIN);
        }

        Date optDate = new Date();
        OrderOptModel optModel = new OrderOptModel();
        optModel.setActorId(actorId);
        optModel.setCreateTime(optDate);
        optModel.setAgencyId(agencyId);
        optModel.setType(2); //人审
        optModel.setRemark(remark);
        optModel.setStage(Stage.ARTIFICIAL_AUTH.key);
        optModel.setStageFlag(Stage.ARTIFICIAL_AUTH.name);
        optModel.setOrderNumber(oldOrder.getOrderNumber());
        optModel.setUserId(oldOrder.getUserId());
        optModel.setOrderTime(oldOrder.getCreateTime());
        optModel.setGuest(oldOrder.getGuest());

        PushEnum.LOAN pushLoan = PushEnum.LOAN.AUTH_PASS;
        if (orderStatus.equals(Status.SUCCESS.key)) { //审核成功
            orderDao.updateOrderStatus(oldOrder.getId(), Stage.LOAN.key, Status.WAIT.key, actorId, optDate);
            optModel.setStatus(Status.SUCCESS.key);
        } else {
            orderDao.updateOrderStatus(oldOrder.getId(), null, Status.FAIL.key, actorId, optDate);
            optModel.setStatus(Status.FAIL.key);
            pushLoan = PushEnum.LOAN.AUTH_REJECT;
        }
        orderOptService.createOrderOpt(optModel);

        //个推
        this.pushMsg(oldOrder, pushLoan, optModel.getStatus(), actorId);


    }

    private void pushMsg(OrderModel oldOrder, PushEnum.LOAN pushLoan, Integer status, Integer actorId) {
        UserModel model = comUserService.selectUserModelById(oldOrder.getUserId());
        if (model == null || StringUtils.isEmpty(model.getGexing())) {
            logger.info("推送>>>放款拒绝,用户id={},没有个推id,无法推送", oldOrder.getUserId());
            return;
        } else {
            PushModel pushModel = comPushTemplateService.generateLoanPushTemplateTitleAndMsg(pushLoan);
            pushModel.setClassify(PushEnum.Classify.SINGLE.value);
            pushModel.setDataId(oldOrder.getOrderNumber());
            pushModel.setUserId(oldOrder.getUserId());
            pushModel.setVestId(model.getVestId());
            pushModel.setDeviceId(model.getGexing());
            jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);
        }

        // 发送审核统计jms
        oldOrder.setReviewId(actorId);
        statisSendHelper.sendReviewStatis(oldOrder, model.getVestId(), oldOrder.getStage(), status, model.getChannelCode());
    }

    @Override
    @Transactional
    public Ret returnReviewOrder(Integer actorId, Integer agencyId, String orderNumber, Integer opeartor) {
        List<String> orderNumbers = Arrays.asList(orderNumber.split(","));
        List<OrderModel> orderList = orderDao.findOrderListByReview(orderNumbers, agencyId);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new BusinessException(AdminRespCode.USER_NOT_REVIEW_ORDER);
        }
        int returnCount = 0;
        for (OrderModel orderModel : orderList) {
            if (!orderModel.getStage().equals(Stage.ARTIFICIAL_AUTH.key)
                    && !orderModel.getStatus().equals(Status.WAIT.key)
                    && !orderModel.getAuthStatus().equals(AuthStatus.OVER_TKE.key)) {
                throw new BusinessException(AdminRespCode.ORDER_STATUS_IS_CHANGE);
            }
            orderDao.returnOrderById(orderModel.getId());
            returnCount++;
            if (logger.isInfoEnabled()) {
                logger.info(">>>orderId={} return by operator={},time={}", orderModel.getId(), opeartor, new Date());
            }
        }
        Ret ret = Ret.create();
        ret.put("count", returnCount);
        return ret;

    }

    @Override
    public Page<OrderPaymentVO> paymentList(PageRow pageRow, OrderPaymentPO po) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        po.setUserPhone(StringUtil.trimToNull(po.getUserPhone()));
        po.setOrderNumber(StringUtil.trimToNull(po.getOrderNumber()));
        po.setChannelCode(StringUtil.trimToNull(po.getChannelCode()));
        int count = orderDao.findPaymentCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        Map<Integer, String> actorMap = new HashMap<>();

        List<OrderPaymentVO> list = orderDao.findPaymentList(po, pageRow);
        for (OrderPaymentVO vo : list) {
            vo.setLoan(FormatUtil.formatDouble2(vo.getLoan()));
            vo.setObtain(FormatUtil.formatDouble2(vo.getObtain()));

            String actorName = actorMap.get(vo.getReviewId());
            if (StringUtil.isNotEmpty(actorName)) {
                vo.setReviewName(actorName);
            } else {
                AdminUserModel adminUser = adminActorDao.getAdminUser(vo.getReviewId());
                if (adminUser != null) {
                    actorMap.put(vo.getReviewId(), adminUser.getName());
                    vo.setReviewName(adminUser.getName());
                }
            }
            vo.setReviewId(null);

            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
        }
        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }


    @Override
    public void rejectPay(List<String> ids, Integer agencyId, Integer actorId, String remark, Date againDate) {
        EntityWrapper<OrderModel> ew = new EntityWrapper<>();
        ew.in("order_number", ids);
        ew.eq("agency_id", agencyId);
        ew.eq("stage", Stage.LOAN.key);
        ew.eq("auth_status", CommonConst.STATUS_VALID);
        ew.in("status", Arrays.asList(1, 8));
        List<OrderModel> orderModels = orderDao.selectList(ew);
        System.out.println();
        orderModels.forEach((e) -> paymentHelper.rejectPayOrder(agencyId, actorId, remark, againDate, e));
    }

    @Override
    public Page<OrderPayRecordVO> paymentRecordList(PageRow pageRow, PaymentRecordPO po) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        po.setSloanDate(StringUtil.trimToNull(po.getSloanDate()));
        po.setEloanDate(StringUtil.trimToNull(po.getEloanDate()));
        po.setUserPhone(StringUtil.trimToNull(po.getUserPhone()));
        po.setPaymentNumber(StringUtil.trimToNull(po.getPaymentNumber()));
        po.setOrderNumber(StringUtil.trimToNull(po.getOrderNumber()));
        po.setChannelCode(StringUtil.trimToNull(po.getChannelCode()));

        int count = paymentFlowDao.findPaymentRecordCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<OrderPayRecordVO> list = paymentFlowDao.findPaymentRecordList(po, pageRow);
        for (OrderPayRecordVO vo : list) {
            vo.setMoney(FormatUtil.formatDouble2(vo.getMoney()));
            if (vo.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(vo.getAgencyId());
                if (agencyModel != null) vo.setAgencyName(agencyModel.getAgencyName());
            }
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }

        }
        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public Page<OrderRepayVO> repayList(PageRow pageRow, RepayListPO po) {
        po.setUserPhone(StringUtil.trimToNull(po.getUserPhone()));
        po.setOrderNumber(StringUtil.trimToNull(po.getOrderNumber()));
        po.setChannelCode(StringUtil.trimToNull(po.getChannelCode()));

        int count = orderDao.findRepayOrderCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<OrderRepayVO> list = orderDao.findRepayOrderCountList(po, pageRow);
        list.forEach(e -> {
            if (e.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            }
            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) e.setVestName(vestModel.getName());
            }

        });

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public void repayConfirm(RepayPO po) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + po.getOrderNumber());
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                OrderModel orderModel = orderDao.selectOrderByOrderNumberAndAgencyId(po.getOrderNumber(), po.getAgencyId());
                if (orderModel == null) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                if (Stage.REPAYMENT.key != orderModel.getStage()) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                List<Integer> wait = Arrays.asList(1, 3, 4);
                if (!wait.contains(orderModel.getStatus())) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                if (po.getRepaymentTime().before(orderModel.getTransferTime())) {
                    throw new BusinessException(AdminRespCode.REPAY_TIME_ERROR);
                }
                paymentHelper.repayConfirm(po, orderModel);

                transactionManager.commit(status);

            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("还款确认程序异常,订单号" + po.getOrderNumber(), e);
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

    @Override
    public Page<OrderRepayRecordVO> repaymentRecordList(PageRow pageRow, RepaymentRecordPO po) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        po.setRepayType(StringUtil.trimToNull(po.getRepayType()));
        po.setUserPhone(StringUtil.trimToNull(po.getUserPhone()));
        po.setOrderNumber(StringUtil.trimToNull(po.getOrderNumber()));
        po.setRepaymentNumber(StringUtil.trimToNull(po.getRepaymentNumber()));
        po.setChannelCode(StringUtil.trimToNull(po.getChannelCode()));

        int count = repaymentFlowDao.findRePaymentRecordCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<OrderRepayRecordVO> list = repaymentFlowDao.findRePaymentRecordList(po, pageRow);

        Map<Integer, String> actorMap = new HashMap<>();
        for (OrderRepayRecordVO vo : list) {
            vo.setMoney(FormatUtil.formatDouble2(vo.getMoney()));
            if (vo.getPayment() != null)
                vo.setPayment(FormatUtil.formatDouble2(vo.getPayment()));
            vo.setAutoRepayment(FormatUtil.formatDouble2(vo.getAutoRepayment()));

            String actorName = actorMap.get(vo.getActorId());
            if (StringUtil.isNotEmpty(actorName)) {
                vo.setOptName(actorName);
            } else {
                AdminUserModel adminUser = adminActorDao.getAdminUser(vo.getActorId());
                if (adminUser != null) {
                    vo.setOptName(adminUser.getName());
                    actorMap.put(vo.getActorId(), adminUser.getName());
                }
            }
            vo.setActorId(null);
            AgencyModel agencyModel = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agencyModel != null) vo.setAgencyName(agencyModel.getAgencyName());
            vo.setAgencyId(null);

            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
        }
        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public PaymentStatisticsVO paymentStatistics(Integer agencyId, Integer actorId) {
        // 获取
        LocalDateTime localDateTime = LocalDateTime.now();

        LocalDateTime minTime = localDateTime.with(LocalTime.MIN);// 今日最小时间
        LocalDateTime maxTime = localDateTime.with(LocalTime.MAX);//今日最大时间

        PaymentStatisticsVO staVO = new PaymentStatisticsVO();

        PaymentStatisticsVO day = paymentFlowDao.findPaymentCountByDate(agencyId, actorId, minTime, maxTime);
        if (day != null) {
            staVO.setPayCountDay(day.getPayCountAll());
            staVO.setPayMoneyDay(FormatUtil.formatDouble2(day.getPayMoneyAll()));
        }

        LocalDateTime monday = minTime.with(DayOfWeek.MONDAY);//周一最小时间
        PaymentStatisticsVO week = paymentFlowDao.findPaymentCountByDate(agencyId, actorId, monday, maxTime);
        if (monday != null) {
            staVO.setPayCountWeek(week.getPayCountAll());
            staVO.setPayMoneyWeek(FormatUtil.formatDouble2(week.getPayMoneyAll()));
        }

        PaymentStatisticsVO all = paymentFlowDao.findPaymentCountByDate(agencyId, actorId, null, null);
        if (all != null) {
            staVO.setPayCountAll(all.getPayCountAll());
            staVO.setPayMoneyAll(FormatUtil.formatDouble2(all.getPayMoneyAll()));
        }

        return staVO;
    }

    @Override
    public void repayDeduct(String orderNumber, Double deductMoney, Integer agencyId, Integer actorId, String remark) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                OrderModel orderModel = orderDao.selectOrderByOrderNumberAndAgencyId(orderNumber, agencyId);
                if (orderModel == null) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                if (Stage.REPAYMENT.key != orderModel.getStage()) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                List<Integer> wait = Arrays.asList(1, 3, 4);
                if (!wait.contains(orderModel.getStatus())) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                Double leftMoney = orderModel.getPayment() - orderModel.getAlreadyMoney();
                if (orderModel.getAlreadyMoney() < 1) {
                    if (deductMoney >= leftMoney) {
                        throw new BusinessException(AdminRespCode.DEDUCT_MONEY_FAIL);
                    }
                }
                if (deductMoney > leftMoney) {
                    throw new BusinessException(AdminRespCode.DEDUCT_LESS_LOAN);
                }

                if (deductMoney.equals(leftMoney)) {
                    orderPaymentHelper.deductComplate(deductMoney, actorId, orderModel);

                } else {
                    orderDao.updateOrderDeductMoney(orderModel.getId(), deductMoney);
                    // 插入操作记录
                    OrderOptModel optModel = new OrderOptModel();
                    optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
                    optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
                    optModel.setStatus(Status.DEDUCT.key);
                    optModel.setType(CommonConst.OK);
                    optModel.setOrderNumber(orderModel.getOrderNumber());
                    optModel.setAgencyId(agencyId);
                    optModel.setActorId(actorId);
                    optModel.setUserId(orderModel.getUserId());
                    optModel.setRemark(remark);
                    optModel.setGuest(orderModel.getGuest());
                    optModel.setOrderTime(orderModel.getCreateTime());
                    orderOptDao.insert(optModel);

                    UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());
                    //减免金额埋点
                    RepayStatisticDTO dto = new RepayStatisticDTO();
                    dto.setRepayDate(Date8Util.date2LocalDate(orderModel.getRepaymentDate()));
                    dto.setAgencyId(orderModel.getAgencyId());
                    dto.setDeductMoney(deductMoney);

                    dto.setVestId(userModel.getVestId());

                    jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
                    logger.info(">>>>发送统计,减免金额jms ,data={}", dto);
                }


                transactionManager.commit(status);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("减免金额程序异常,订单号" + orderNumber, e);
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


    @Override
    public void paymentReturn(String orderNumber, String remark, Integer actorId, Integer agencyId) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                OrderModel orderModel = orderDao.selectOrderByOrderNumberAndAgencyId(orderNumber, agencyId);
                if (orderModel == null) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                if (Stage.LOAN.key != orderModel.getStage()) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                if (Status.WAIT.key != orderModel.getStatus()) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }
                OrderModel update = new OrderModel();
                update.setId(orderModel.getId());
                update.setStage(Stage.ARTIFICIAL_AUTH.key);
                update.setStatus(Status.WAIT.key);
                orderDao.updateById(update);


                // 插入操作记录
                OrderOptModel optModel = new OrderOptModel();
                optModel.setStage(OrderConstant.Stage.LOAN.key);
                optModel.setStageFlag(OrderConstant.Stage.LOAN.name);
                optModel.setStatus(Status.OVERDUE.key);
                optModel.setType(CommonConst.OK);
                optModel.setOrderNumber(orderModel.getOrderNumber());
                optModel.setAgencyId(agencyId);
                optModel.setActorId(actorId);
                optModel.setUserId(orderModel.getUserId());
                optModel.setGuest(orderModel.getGuest());
                optModel.setRemark(remark);
                orderOptDao.insert(optModel);

                transactionManager.commit(status);

                //发送信审统计
                UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());
                statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), Stage.LOAN.key, Status.OVERDUE.key, userModel.getChannelCode());
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("放款退回异常,订单号" + orderNumber, e);
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

    @Override
    public void exportOrderList(OrderPo orderPo, HttpServletResponse response) {
        if (StringUtils.isNotBlank(orderPo.getStatus())) {
            String[] split = orderPo.getStatus().split(",");
            orderPo.setOrderStage(Integer.valueOf(split[0]));
            orderPo.setOrderStatus(Integer.valueOf(split[1]));
            if (split.length == 3) {
                orderPo.setOrderAuthStatus(Integer.valueOf(split[2]));
            }
        }
        Integer count = orderDao.queryOrderCount(orderPo);

        if (count == 0) {
            throw new BusinessException(AdminRespCode.NOT_HAVE_ORDER);
        }
        if (count > 20000) {
            throw new BusinessException(AdminRespCode.RESPONSE_DATA_SIZE_LONG);
        }

        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder("订单列表");
        excelWriteBuilder
                .addHeaderColumn("订单号", "orderNumber")
                .addHeaderColumn("用户姓名", "userName")
                .addHeaderColumn("用户手机", "userPhone")
                .addHeaderColumn("渠道名称", "channelName")
                .addHeaderColumn("借款期限", "days")
                .addHeaderColumn("状态", "orderStatusName")
                .addHeaderColumn("借款金额", "loan")
                .addHeaderColumn("综合费", "stampTax")
                .addHeaderColumn("实际到账金额", "obtain")
                .addHeaderColumn("逾期天数", "exceedDays")
                .addHeaderColumn("逾期费用", "exceedFee")
                .addHeaderColumn("应还金额", "payment")
                .addHeaderColumn("已还金额", "alreadyMoney")
                .addHeaderColumn("还款减免金额", "deductMoney")
                .addHeaderColumn("下单时间", "createTime")
                .addHeaderColumn("到账时间", "transferTime")
                .addHeaderColumn("应还日期", "repaymentDate")
                .addHeaderColumn("实际还款时间", "repaymentTime")
                .addHeaderColumn("备注", "remark")
                .addHeaderColumn("客群", "guest");
        int totalRow = 1000;
        // 默认查询
        orderPo.setPage(0);
        orderPo.setPageSize(totalRow);
        orderPo.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            List<OrderPageVo> list = orderDao.queryOrderPage(orderPo);

            for (OrderPageVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();
                obj.put("createTime", dateFormat.format(vo.getCreateTime()));
                obj.put("transferTime", vo.getTransferTime() == null ? "---" : dateFormat.format(vo.getTransferTime()));
                obj.put("repaymentTime", vo.getRepaymentTime() == null ? "---" : dateFormat.format(vo.getRepaymentTime()));
                obj.put("repaymentDate", vo.getRepaymentDate() == null ? "---" : dayFormat.format(vo.getRepaymentDate()));
                obj.put("userName", vo.getUserName() == null ? "---" : vo.getUserName());
                obj.put("userPhone", vo.getUserPhone() == null ? "---" : vo.getUserPhone());
                obj.put("channelName", vo.getChannelName() == null ? "---" : vo.getChannelName());
                obj.put("orderNumber", vo.getOrderNumber() == null ? "---" : vo.getOrderNumber());
                obj.put("days", vo.getDays());
                obj.put("loan", vo.getLoan());
                obj.put("stampTax", vo.getStampTax());
                obj.put("obtain", vo.getObtain());
                obj.put("exceedDays", vo.getExceedDays());
                obj.put("exceedFee", vo.getExceedFee());
                obj.put("payment", vo.getPayment());
                obj.put("alreadyMoney", vo.getAlreadyMoney());
                obj.put("deductMoney", vo.getDeductMoney());
                obj.put("remark", vo.getRemark() == null ? "---" : vo.getRemark());
                if (vo.getGuest() != null) {
                    if (vo.getGuest() == OrderConstant.Guest.NEW_GUEST.key) {
                        obj.put("guest", OrderConstant.Guest.NEW_GUEST.name);
                    } else
                        obj.put("guest", OrderConstant.Guest.OLD_GUEST.name);
                } else {
                    obj.put("guest", "---");
                }
                String statusName = OrderConstant.handleOrderStatus(vo.getStage(), vo.getStatus());
                obj.put("orderStatusName", statusName == null ? "---" : statusName);

                excelWriteBuilder.appendRowData(obj);
            }
            orderPo.initLimit();
            if (list.size() < totalRow) break;
            orderPo.setPage(orderPo.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, "订单列表", excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }


}
