package org.songbai.loan.admin.order.listener;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.vo.risk.RiskOrderResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/22 3:35 PM
 */
@Component
public class RiskOrderListener {
    private static final Logger logger = LoggerFactory.getLogger(RiskOrderListener.class);
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComPushTemplateService pushTemplateService;
    @Autowired
    private StatisSendHelper statisSendHelper;

    /**
     * 风控审核结果接收
     */
    @JmsListener(destination = JmsDest.RISK_ORDER_RESULT)
    public void riskResultOrder(RiskOrderResultVO resultVO) {

        if (resultVO == null) {
            logger.info("接收风控审核结果为空");
            return;
        }
        logger.info("接收风控审核结果,resultVO={}", resultVO);

        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + resultVO.getOrderNumber());
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {

                OrderModel selectOrder = new OrderModel();
                selectOrder.setOrderNumber(resultVO.getOrderNumber());

                UserInfoModel u = comUserService.selectUserInfoByThridId(resultVO.getUserId());
                if (u == null) {
                    logger.info("用户不存在,第三方id={}", resultVO.getUserId());
                    throw new RuntimeException("用户不存在,第三方id=" + resultVO.getUserId());
                }
                selectOrder.setUserId(u.getUserId());

                OrderModel orderModel = orderDao.selectOne(selectOrder);
                if (orderModel == null) {
                    logger.warn("用户借款订单不存在,用户id={},订单号={}", resultVO.getUserId(), resultVO.getOrderNumber());
                    throw new RuntimeException("用户借款订单不存在,用户id=" + resultVO.getUserId() + "订单号={}" + resultVO.getOrderNumber());
                }
                if (orderModel.getStage() != OrderConstant.Stage.MACHINE_AUTH.key && orderModel.getStatus() != OrderConstant.Status.WAIT.key) {
                    throw new RuntimeException("用户借款订单不是机审阶段状态,用户id=" + resultVO.getUserId() + "订单号={}" + resultVO.getOrderNumber());
                }

                boolean isPush = false;
                String remark = resultVO.getResultMsg();


                if (StringUtil.isNotEmpty(remark)) {
                    if (remark.length() > 150) {
                        remark = remark.substring(0, 150);
                    }
                }
                int statisStatus = OrderConstant.Status.SUCCESS.key;

                OrderModel updateOrder = new OrderModel();
                updateOrder.setId(orderModel.getId());

                if (resultVO.getResult() == RiskConst.Result.DEFAULT.code) {//待复审
                    updateOrder.setStage(OrderConstant.Stage.ARTIFICIAL_AUTH.key);
                    updateOrder.setStatus(OrderConstant.Status.WAIT.key);
                    //remark = "机审成功,待人工复审";

                } else if (resultVO.getResult() == RiskConst.Result.PASS.code) {//通过

                    updateOrder.setStage(OrderConstant.Stage.LOAN.key);
                    updateOrder.setStatus(OrderConstant.Status.WAIT.key);
                    //remark = "机审成功,财务放款";
                    statisStatus = OrderConstant.Status.PROCESSING.key;//暂用来表示不经过复审

                } else if (resultVO.getResult() == RiskConst.Result.REJECT.code) {// 拒绝
                    updateOrder.setStatus(OrderConstant.Status.FAIL.key);
                    statisStatus = OrderConstant.Status.FAIL.key;
                    //remark = "机审失败";
                    isPush = true;

                } else {
                    logger.warn("风控审核状态类型不存在,用户id={},订单号={}", resultVO.getUserId(), resultVO.getOrderNumber());
                    return;
                }

                orderDao.updateById(updateOrder);

                OrderOptModel optModel = new OrderOptModel();
                optModel.setOrderNumber(orderModel.getOrderNumber());
                optModel.setAgencyId(orderModel.getAgencyId());
                optModel.setUserId(orderModel.getUserId());
                optModel.setType(CommonConst.YES);
                optModel.setGuest(orderModel.getGuest());
                optModel.setStage(OrderConstant.Stage.MACHINE_AUTH.key);
                optModel.setStageFlag(OrderConstant.Stage.MACHINE_AUTH.name);
                optModel.setStatus(statisStatus);
                optModel.setOrderTime(orderModel.getCreateTime());
                optModel.setCreateTime(new Date());
                UserModel model = comUserService.selectUserModelById(orderModel.getUserId());
                if (model != null) {
                    optModel.setGuest(model.getGuest());
                }
                optModel.setRemark(remark);

                orderOptDao.insert(optModel);

                transactionManager.commit(status);

                if (isPush) {
                    if (model == null || StringUtils.isEmpty(model.getGexing())) {
                        logger.info("推送>>>机审失败,用户id={},没有个推id,无法推送", orderModel.getUserId());
                        return;
                    } else {
                        PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.AUTH_REJECT);

                        pushModel.setClassify(PushEnum.Classify.SINGLE.value);
                        pushModel.setDataId(orderModel.getOrderNumber());
                        pushModel.setUserId(orderModel.getUserId());
                        pushModel.setVestId(model.getVestId());
                        pushModel.setDeviceId(model.getGexing());
                        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);
                    }
                }

                //统计行为jms
                statisSendHelper.sendReviewStatis(orderModel, model.getVestId(), OrderConstant.Stage.MACHINE_AUTH.key, statisStatus, model.getChannelCode());
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("风控程序异常,用户id=," + resultVO.getUserId() + ",订单号" + resultVO.getOrderNumber(), e);
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

}
