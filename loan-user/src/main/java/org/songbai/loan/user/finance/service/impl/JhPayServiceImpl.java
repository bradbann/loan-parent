package org.songbai.loan.user.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.helper.JhPayHelper;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.FinanceConstant.PayPlatform;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.OrderConstant.RepayType;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.JhPayModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.FinanceIOService;
import org.songbai.loan.user.finance.service.JhPayService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class JhPayServiceImpl implements JhPayService {
    private static final Logger logger = LoggerFactory.getLogger(JhPayService.class);
    //二维码生成地址
    private static final String JhQrCodeUrl = "http://jh.chinambpc.com/createQrcode?url=";
    @Autowired
    FinanceIOService financeIOService;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    ComUserService comUserService;
    @Autowired
    BasicOrderService basicOrderService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    OrderDao orderDao;
    @Autowired
    FinanceIODao ioDao;
    @Autowired
    JhPayHelper jhPayHelper;
    @Autowired
    SpringProperties properties;


    @Override
    public Ret pay(String orderNum, String payCode, Integer userId) {
        //校验订单状态
        OrderModel orderModel = financeIOService.validateOrder(orderNum, userId);

        UserModel userModel = comUserService.selectUserModelById(userId);
        if (redisTemplate.opsForHash().hasKey(UserRedisKey.USER_REPAYMENT, userId)) {
            throw new BusinessException(UserRespCode.ORDER_HAS_REPAYMENT);
        }

        if (orderModel.getPayment().compareTo(1.00D) < 0 || orderModel.getPayment().compareTo(3000D) > 0) {
            throw new BusinessException(UserRespCode.NUMBER_IS_LAGER);
        }

        String requestId = OrderIdUtil.getRepaymentId();
        Integer payType = RepayType.ALIPAY.key;
        if (StringUtils.isNotBlank(payCode) && payCode.equals(PayPlatform.WXPAY.code)) payType = RepayType.WEIXIN.key;

        //生成支付地址
        String payUrl = jhPayHelper.createPayUrl(orderModel, payCode, requestId);

        //更新订单状态
        lockOrderAndInitOrder(userModel, orderModel, requestId, payCode, payType, orderNum);


        Ret ret = Ret.create();
        ret.put("url", payUrl);
        ret.put("requestId", requestId);
        redisTemplate.opsForHash().put(UserRedisKey.USER_REPAYMENT, userId, requestId);
        return ret;
    }

    @Override
    public Ret scanPay(String orderNum, String payCode, Integer userId) {
        //校验订单状态
        OrderModel orderModel = financeIOService.validateOrder(orderNum, userId);

        UserModel userModel = comUserService.selectUserModelById(userId);
        //一直生成
        if (redisTemplate.opsForHash().hasKey(UserRedisKey.USER_REPAYMENT, userId)) {
            throw new BusinessException(UserRespCode.ORDER_HAS_REPAYMENT);
        }

        if (orderModel.getPayment().compareTo(1.00D) < 0 || orderModel.getPayment().compareTo(3000D) > 0) {
            throw new BusinessException(UserRespCode.NUMBER_IS_LAGER);
        }

        String requestId = OrderIdUtil.getRepaymentId();
        Integer payType = RepayType.ALIPAY.key;
        if (StringUtils.isNotBlank(payCode) && payCode.equals(PayPlatform.WXPAY.code)) payType = RepayType.WEIXIN.key;

        //生成支付地址
        String payUrl = jhPayHelper.createScanPayUrl(orderModel, payCode, requestId);
        String result = JhPayHelper.sendPost(payUrl, null);
        JSONObject json = JSONObject.parseObject(result);
        if (json.get("code") != null) {
            logger.error("jhPayService scanPay is error,msg={}", json.getString("message"));
            throw new BusinessException(UserRespCode.SYSTEM_EXCEPTION);
        }
        String resultPayUrl = json.getString("url");
        String resultUrl = JhQrCodeUrl + resultPayUrl;
        System.out.println("链接地址>>>>>" + resultUrl);

        //更新订单状态
        lockOrderAndInitOrder(userModel, orderModel, requestId, payCode, payType, orderNum);

        Ret ret = Ret.create();
        ret.put("url", resultUrl);
        ret.put("requestId", requestId);

        redisTemplate.opsForHash().put(UserRedisKey.USER_REPAYMENT, userId, requestId);
        return ret;
    }

    @Override
    public void jhPayNotify(JhPayModel jhPayModel) {
        //校验聚合回调基本参数
        checkJhPayParam(jhPayModel);

        //查询流水
        FinanceIOModel ioModel = new FinanceIOModel();
        ioModel.setRequestId(jhPayModel.getMerchantOutOrderNo());
        ioModel = ioDao.selectOne(ioModel);
        if (ioModel == null) {
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }
        //同一笔requestId已处理的话就return,返回success，不再通知,失败的订单可以继续接受
        if (ioModel.getStatus() != FinanceConstant.IoStatus.PROCESSING.key) {
            logger.info("jhPayService notify this order has been deal,again request,requestId={}", jhPayModel.getMerchantOutOrderNo());
            return;
        }
        ioModel.setThirdOrderId(jhPayModel.getAliNo());

        //校验订单
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        if (orderModel == null) {
            throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
        }

        //TODO 不校验订单状态,回调就插入流水，允许多次还款
//        if (orderModel.getStage() != Stage.REPAYMENT.key && orderModel.getStatus() != Status.PROCESSING.key) {
//            logger.error("jhPayService notify orderStatus is change,stage={},status={}", orderModel.getStage(), orderModel.getStatus());
//            throw new BusinessException(UserRespCode.ORDER_HAS_FAILED);
//        }

        //校验支付金额
        JSONObject msgJson = JSON.parseObject(jhPayModel.getMsg());
        Double shouldPay = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());//应还金额-已还金额
        if (msgJson.get("payMoney") == null
                || msgJson.getDouble("payMoney").compareTo(shouldPay) < 0) {
            logger.error("jhPayService notify money is error,orderMoney={},newMoney={}", orderModel.getPayment(), msgJson.getDouble("payMoney"));
            lockOrderAndUpdateIo(ioModel);
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }

        if (!jhPayHelper.checkSign(jhPayModel, orderModel.getAgencyId())) {//密码校验失败
            logger.error("jhPayService notify sign is error,info={},agencyId={}", jhPayModel, orderModel.getAgencyId());
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }

        //订单处理
        lockOrderAndDealSucc(ioModel);
    }


    @Override
    public String dealJhOrder(FinanceIOModel ioModel) {
        String msg = "支付成功";
        if (ioModel.getStatus() == FinanceConstant.IoStatus.SUCCESS.key) {
            logger.info("jhPayService io has been deal succ,orderNumber={},requestId={}", ioModel.getOrderId(), ioModel.getRequestId());
            return msg;
        } else if (ioModel.getStatus() == FinanceConstant.IoStatus.FAILED.key) {
            logger.info("jhPayService io has been deal fail,orderNumber={},requestId={}", ioModel.getOrderId(), ioModel.getRequestId());
            return "支付失败";
        }

        JhPayModel model = jhPayHelper.queryOrderStatus(ioModel.getRequestId(), ioModel.getAgencyId());
        if (model == null) return "系统异常，请稍后再试。";
        if (model.getCode() != null) {//失败错误码
            logger.error("jhPayService querySerive is error,code={},msg={}", model.getCode(), model.getMsg());
            lockOrderAndDealFail(ioModel, model.getMsg());
            return "支付失败";
        }
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        Integer orderStage = orderModel.getStage();
        if (!orderStage.equals(OrderConstant.Stage.REPAYMENT.key)) {
            logger.info(">>>>order stage is change,orderNumber={}", ioModel.getOrderId());
            lockOrderAndUpdateIo(ioModel);
            return msg;
        }
        Integer orderStatus = orderModel.getStatus();
        if (orderStatus.equals(OrderConstant.Status.SUCCESS.key) || orderStatus == OrderConstant.Status.OVERDUE_LOAN.key
                || orderStatus == OrderConstant.Status.ADVANCE_LOAN.key || orderStatus == OrderConstant.Status.CHASE_LOAN.key) {
            logger.info(">>>>order status is change,orderNumber={}", ioModel.getOrderId());
            lockOrderAndUpdateIo(ioModel);
            return msg;
        }


        Instant instant = ioModel.getCreateTime().toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime ioCreateTime = LocalDateTime.ofInstant(instant,zone);
        LocalDateTime now = LocalDateTime.now().minusMinutes(properties.getInteger("user:jh:order:timeout",30));

        if (model.getPayResult() == CommonConst.NO || (model.getPayResult() == 3 && ioCreateTime.isBefore(now))) {//交易失败
            lockOrderAndDealFail(ioModel, "订单超时支付,自动失败!");
            return "订单超时支付,自动失败!";
        }


        if (model.getPayResult() == 3) {//处理中
//            throw new BusinessException(UserRespCode.ORDER)
            return "订单待处理中,请稍后";
        }

        if (model.getPayResult() == CommonConst.YES) {//交易成功
            lockOrderAndDealSucc(ioModel);
        }
        return msg;
    }


    private void lockOrderAndUpdateIo(FinanceIOModel ioModel) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + ioModel.getOrderId());
            lock.lock();
            basicOrderService.repaymentUpdateIo(ioModel);
            redisTemplate.opsForHash().delete(UserRedisKey.USER_REPAYMENT, ioModel.getUserId());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void lockOrderAndInitOrder(UserModel userModel, OrderModel orderModel, String requestId, String payCode, Integer payType, String orderNum) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNum);
            lock.lock();

            //成功创建订单流水表
            basicOrderService.initOrder(userModel, orderModel, requestId, null, payCode, payType);
            basicOrderService.updateOrderStatus(OrderConstant.Stage.REPAYMENT.key, OrderConstant.Status.PROCESSING.key, orderNum);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void lockOrderAndDealSucc(FinanceIOModel ioModel) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + ioModel.getOrderId());
            lock.lock();
            basicOrderService.repaymentSuccess(ioModel);
            logger.info("jhPayService userId={} pay succ,orderNum={},money={}", ioModel.getUserId(), ioModel.getOrderId(), ioModel.getMoney());
            redisTemplate.opsForHash().delete(UserRedisKey.USER_REPAYMENT, ioModel.getUserId());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void lockOrderAndDealFail(FinanceIOModel ioModel, String errorMsg) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + ioModel.getOrderId());
            lock.lock();
            basicOrderService.dealOrderFailed(ioModel, errorMsg, true);
            redisTemplate.opsForHash().delete(UserRedisKey.USER_REPAYMENT, ioModel.getUserId());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private void checkJhPayParam(JhPayModel jhPayModel) {
        if (jhPayModel == null || jhPayModel.getPayResult() == null || StringUtils.isEmpty(jhPayModel.getMsg())
                || StringUtils.isEmpty(jhPayModel.getId()) || jhPayModel.getPayResult() != CommonConst.YES) {
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }
    }

}
