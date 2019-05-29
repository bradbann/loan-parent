package org.songbai.loan.admin.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.service.OrderMachineService;
import org.songbai.loan.admin.order.vo.OrderMachineVo;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderMachineServiceImpl implements OrderMachineService {
    Logger logger = LoggerFactory.getLogger(OrderMachineService.class);
    @Autowired
    OrderDao orderDao;
    @Autowired
    ComAgencyService comAgencyService;
    @Autowired
    DistributeLockFactory lockFactory;
    @Autowired
    DataSourceTransactionManager transactionManager;
    @Autowired
    OrderOptDao orderOptDao;
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private StatisSendHelper statisSendHelper;


    @Override
    public Page<OrderMachineVo> findMachineFailPage(OrderPo po) {
        Integer count = orderDao.getMachineFailCount(po);
        if (count == 0) return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        List<OrderMachineVo> list = orderDao.findMachineFailList(po);
        list.forEach(e -> {
            AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
            if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            e.setAgencyId(null);
        });
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public void updateMachineOrderStatus(String orderNumber, Integer agencyId, Integer actorId) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);

            try {
                OrderModel order = orderDao.selectOrderByOrderNumberAndAgencyId(orderNumber, agencyId);
                if (order == null) {
                    throw new BusinessException(AdminRespCode.ORDER_NOT_EXISIT);
                }
                if (order.getStage() != OrderConstant.Stage.MACHINE_AUTH.key
                        || order.getStatus() != OrderConstant.Status.FAIL.key) {
                    logger.info(">>>>machineOrder order is error,orderNumber={},stage={},status={}", order.getOrderNumber(), order.getStage(), order.getStatus());
                    throw new BusinessException(AdminRespCode.ORDER_STATUS_IS_CHANGE);
                }

                OrderModel update = new OrderModel();
                update.setId(order.getId());
                update.setStatus(OrderConstant.Status.WAIT.key);
                update.setStage(OrderConstant.Stage.ARTIFICIAL_AUTH.key);
                orderDao.updateById(update);

                // 插入操作记录
                OrderOptModel optModel = new OrderOptModel();
                optModel.setStage(OrderConstant.Stage.MACHINE_AUTH.key);
                optModel.setStageFlag(OrderConstant.Stage.MACHINE_AUTH.name);
                optModel.setStatus(OrderConstant.Status.OVERDUE.key);
                optModel.setType(CommonConst.OK);
                optModel.setOrderNumber(order.getOrderNumber());
                optModel.setAgencyId(agencyId);
                optModel.setGuest(order.getGuest());
                optModel.setActorId(actorId);
                optModel.setUserId(order.getUserId());
                optModel.setRemark("机审失败转复审");
                orderOptDao.insert(optModel);

                transactionManager.commit(status);

                //发送信审统计
                UserModel userModel = comUserService.selectUserModelById(order.getUserId());
                statisSendHelper.sendReviewStatis(order, userModel.getVestId(), OrderConstant.Stage.MACHINE_AUTH.key, OrderConstant.Status.OVERDUE.key, userModel.getChannelCode());

            } catch (Exception e) {
                logger.error("update machineOrder to review is fail,订单号" + orderNumber, e);
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

