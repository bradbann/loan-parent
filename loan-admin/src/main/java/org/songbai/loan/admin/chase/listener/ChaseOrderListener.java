package org.songbai.loan.admin.chase.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.loan.admin.agency.dao.AgencyDao;
import org.songbai.loan.admin.chase.service.ChaseService;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.product.dao.ProductDao;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.loan.ProductModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.songbai.loan.constant.JmsDest.CHASE_ORDER_STATUS;

@Component
public class ChaseOrderListener {
    private static final Logger logger = LoggerFactory.getLogger(ChaseOrderListener.class);
    @Autowired
    OrderDao orderDao;
    @Autowired
    ChaseService chaseService;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private AgencyDao agencyDao;
    @Autowired
    StatisSendHelper statisSendHelper;

    @JmsListener(destination = CHASE_ORDER_STATUS)
    public void handlerOrder(JSONObject jsonObject) {
        logger.info(">>>>>>>>order chase status is start!jsonObject={}", jsonObject);

        int page = 0;
        int pageSize = 50;

        LocalDate today = LocalDate.now();

        while (true) {
            Integer limit = page * pageSize;

            List<OrderModel> list = orderDao.queryChaseOrderList(today, limit, pageSize);
            if (CollectionUtils.isEmpty(list)) {
                logger.debug(">>>>order chase list is empty");
            }

            Map<Integer, Integer> map = new HashMap<>();

            for (OrderModel model : list) {

                LocalDate exceedDate = Date8Util.date2LocalDate(model.getRepaymentDate()).plusDays(model.getExceedDays());
                if (exceedDate.isEqual(today)) {
                    logger.info(">>>>order already calc,id={}", model.getId());
                    continue;
                }

                ProductModel productModel = productDao.selectById(model.getProductId());
                if (productModel == null) {
                    logger.info(">>>>product is null,orderId={}", model.getId());
                    continue;
                }

                DistributeLock lock = null;
                try {
                    lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + model.getOrderNumber());
                    lock.lock();

                    LocalDate plusDay = Date8Util.date2LocalDate(model.getRepaymentDate()).plusDays(model.getExceedDays());
                    if (plusDay.equals(today)) {
                        logger.info(">>>>订单逾期已经计算过>>>,model={}", model);
                        continue;
                    }
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    TransactionStatus status = transactionManager.getTransaction(def);
                    try {

                        Double exceedFee = 0D;//逾期费用

                        Integer badDebt = map.get(model.getAgencyId());
                        if (badDebt == null) {
                            AgencyModel agencyModel = agencyDao.selectById(model.getAgencyId());
                            if (agencyModel == null) {
                                map.put(model.getAgencyId(), 30);
                            } else {
                                map.put(model.getAgencyId(), agencyModel.getBadDebt());
                            }
                            badDebt = map.get(model.getAgencyId());

                        }
                        boolean isBad = false;

                        UserModel userModel = comUserService.selectUserModelById(model.getUserId());


                        if (model.getExceedDays().equals(badDebt)) {

                            logger.info(">>>>order 逾期天数{}天，自动设置为坏账。id={}", badDebt, model.getId());
                            OrderModel update = new OrderModel();
                            update.setId(model.getId());
                            update.setStatus(OrderConstant.Status.FAIL.key);
                            orderDao.updateById(update);
                            isBad = true;


                            RepayStatisticDTO dto = new RepayStatisticDTO();
                            dto.setRepayDate(Date8Util.date2LocalDate(model.getRepaymentDate()));
                            dto.setAgencyId(model.getAgencyId());
                            dto.setIsFail(CommonConst.YES);
                            dto.setVestId(userModel.getVestId());

                            jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
                            logger.info(">>>>发送统计,坏账jms ,data={}", dto);

                            OrderOptModel optModel = new OrderOptModel();
                            optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
                            optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
                            optModel.setStatus(OrderConstant.Status.FAIL.key); //坏账
                            optModel.setType(CommonConst.YES);
                            optModel.setOrderNumber(model.getOrderNumber());
                            optModel.setAgencyId(model.getAgencyId());
                            optModel.setUserId(model.getUserId());
                            optModel.setGuest(model.getGuest());
                            optModel.setRemark("逾期天数已超过" + model.getExceedDays() + "天,系统自动设置为坏账");
                            orderOptDao.insert(optModel);
                        }

                        if (model.getExceedDays() >= productModel.getExceedDays()) {
                            logger.info(">>>>order 逾期天数已超过{}天，不再计算逾期费用。id={}", model.getExceedDays(), model.getId());

                        } else {
                            exceedFee = productModel.getExceedFee();
                        }

                        Integer orderStatus = OrderConstant.Status.OVERDUE.key;


                        if (model.getExceedDays() == 0) {
                            // 首逾埋点
                            RepayStatisticDTO dto = new RepayStatisticDTO();
                            dto.setRepayDate(Date8Util.date2LocalDate(model.getRepaymentDate()));
                            dto.setAgencyId(model.getAgencyId());
                            dto.setIsOnOverdue(CommonConst.YES);
                            dto.setOverdueMoney(exceedFee);
                            dto.setVestId(userModel.getVestId());
                            dto.setIsOnFirstOverdue(CommonConst.YES);
                            jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
                            logger.info(">>>>发送统计,首逾jms ,data={}", dto);


                            //插入操作记录
                            OrderOptModel optModel = new OrderOptModel();
                            optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
                            optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
                            optModel.setStatus(OrderConstant.Status.OVERDUE.key);
                            optModel.setType(CommonConst.YES);
                            optModel.setOrderNumber(model.getOrderNumber());
                            optModel.setAgencyId(model.getAgencyId());
                            optModel.setGuest(model.getGuest());
                            optModel.setUserId(model.getUserId());

                            String time = Date8Util.date2LocalDate(model.getRepaymentDate()).toString() + " 23:59:59";

                            optModel.setRemark("订单已于" + time + "到期，目前逾期1天");

                            orderOptDao.insert(optModel);

                            //信审统计
                            statisSendHelper.sendReviewStatis(model, userModel.getVestId(), OrderConstant.Stage.REPAYMENT.key, OrderConstant.Status.OVERDUE.key, userModel.getChannelCode());

                        } else {
                            if (exceedFee > 0) {
                                //逾期埋点
                                RepayStatisticDTO dto = new RepayStatisticDTO();
                                dto.setRepayDate(Date8Util.date2LocalDate(model.getRepaymentDate()));
                                dto.setAgencyId(model.getAgencyId());
                                dto.setIsOnOverdue(CommonConst.YES);
                                dto.setOverdueMoney(exceedFee);
                                dto.setVestId(userModel.getVestId());
                                jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
                                logger.info(">>>>发送统计,逾期jms ,data={}", dto);
                            }
                        }


                        if (isBad) {
                            orderStatus = null;
                        }
                        orderDao.updateOrderOverdue(model.getId(), exceedFee, orderStatus);

                        transactionManager.commit(status);
                    } catch (Exception e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("还款确认程序异常,订单号" + model.getOrderNumber(), e);
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
            if (list.size() < pageSize) break;
            page++;
        }
    }


}