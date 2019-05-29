package org.songbai.loan.user.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.finance.YibaoConstant;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.RepaymentService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 易宝支付service
 *
 * @author wjl
 * @date 2018年11月12日 16:31:37
 * @description
 */
@Service("yiBaoPayService")
public class YiBaoPayServiceImpl implements RepaymentService {

    private static final Logger log = LoggerFactory.getLogger(YiBaoPayServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private BasicOrderService basicOrderService;
    @Autowired
    private FinanceIODao ioDao;
    @Autowired
    private ComFinanceService comFinanceService;
    @Autowired
    private SpringProperties springProperties;

    @Override
    public void pay(OrderModel orderModel, UserModel userModel, UserInfoModel userInfoModel, UserBankCardModel userBankCardModel) {
        Integer userId = userModel.getId();
        Integer agencyId = userModel.getAgencyId();
        String oldOrderId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
        if (StringUtils.isNotBlank(oldOrderId)) {
            payCodeResend(agencyId, oldOrderId);
            return;
        }
        String bankCardNum = userBankCardModel.getBankCardNum();
        String orderNum = orderModel.getOrderNumber();
        Double money = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());
        String requestId = OrderIdUtil.getRepaymentId();
        String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
        //请求易宝
        YopRequest yopRequest = payParam(userModel, userBankCardModel, money, userInfoModel, bankCardNum, requestId, appKey);
        log.info("user:{} request yiBao pay , param:{}", userId, JSON.toJSONString(yopRequest));
        Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/unified/firstpay", yopRequest);
        log.info("user:{} request yiBao pay the returned result：{}", userId, JSON.toJSONString(result));
        if (!(StringUtils.isBlank(result.get("errormsg")) && result.get("status").equals("TO_VALIDATE"))) {// 失败则进行相关处理
            log.info("用户：{}请求易宝支付受理失败,参数为：{}", userId, JSON.toJSONString(yopRequest));
            throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, result.get("errormsg"));
        }
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNum);
            lock.lock();
            //成功创建订单流水表
            basicOrderService.initOrder(userModel, orderModel, requestId, bankCardNum, FinanceConstant.PayPlatform.YIBAO.code, OrderConstant.RepayType.BANKCARD.key);
            redis.opsForHash().put(UserRedisKey.USER_REPAYMENT, userId, requestId);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }

    }

    @Override
    public void payConfirm(String code, Integer userId) {
        String requestId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
        if (StringUtils.isBlank(requestId)) {
            throw new BusinessException(UserRespCode.REGET_MSG);
        }
        FinanceIOModel ioModel = getAndCheckIoModel(requestId);
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        Double realPayMoney = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());
        if (Double.compare(ioModel.getMoney(), realPayMoney) != 0.0) {//说明订单金额不一样
            log.info("订单金额ioModel:{}与orderModel:{}不一致导致订单失效，请查明原因", ioModel.getMoney(), realPayMoney);
            basicOrderService.dealOrderFailed(ioModel, "订单金额ioModel:" + ioModel.getMoney() + "与orderModel:" + realPayMoney + "不一致导致订单失效，请查明原因", false);
            throw new BusinessException(UserRespCode.ORDER_HAS_FAILED);
        }
        String appKey = comFinanceService.getYiBaoSellIdByAgencyId(orderModel.getAgencyId());
        YopRequest yopRequest = new YopRequest(appKey);
        yopRequest.addParam("merchantno", appKey.substring(4));
        yopRequest.addParam("requestno", requestId);
        yopRequest.addParam("validatecode", code);
        log.info("user:{} request yiBao pay confirm , param:{}", userId, JSON.toJSONString(yopRequest));
        Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/firstpayorder/confirm", yopRequest);
        log.info("user:{} request yiBao pay confirm returned result:{}", userId, JSON.toJSONString(result));
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + userId);
            lock.lock();
            if (StringUtils.isBlank(result.get("errormsg")) && result.get("status").equals("PROCESSING")) {//订单受理成功
                basicOrderService.dealOrderSuccess(ioModel);
                return;
            }
            if (StringUtils.isNotBlank(result.get("errormsg"))) {
                if (result.get("status").equals("TO_VALIDATE")) {//验证码错了
                    log.info("用户：{}请求易宝支付确认接口受理失败,原因为：{}", userId, result.get("errormsg"));
                    throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, result.get("errormsg"));
                } else {
                    if (result.get("errormsg").contains("余额不足")) {
                        redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
                        throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, "银行卡余额不足");
                    }
                    if (result.get("errormsg").contains("订单不存在")) {
                        redis.opsForHash().delete(UserRedisKey.USER_REPAYMENT, userId);
                        throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, "订单超时，请重新支付");
                    }
                    log.info("用户：{}请求易宝支付确认接口受理失败,参数为：{}", userId, JSON.toJSONString(yopRequest));
                    basicOrderService.dealOrderFailed(ioModel, result.get("errormsg"), false);
                    throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, result.get("errormsg"));
                }
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void payCodeResend(Integer agencyId, String oldOrderId) {
        String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
        YopRequest yopRequest = new YopRequest(appKey);
        yopRequest.addParam("merchantno", appKey.substring(4));
        yopRequest.addParam("requestno", oldOrderId);
        yopRequest.addParam("advicesmstype", "MESSAGE");
        Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/firstpayorder/resend", yopRequest);
        log.info("请求【易宝支付重发】支付结果：{}", JSON.toJSONString(result));
        if (result.get("status").equals("TO_VALIDATE")) {
            log.info("request yibao pay codeResend success");
        } else {
            log.info("请求易宝短信重发受理失败");
            FinanceIOModel ioModel = getAndCheckIoModel(oldOrderId);
            basicOrderService.dealOrderFailed(ioModel, result.get("errormsg"), true);
            throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED, result.get("errormsg"));
        }
    }

    @Override
    public PayResultVO deductPay(PayOrderVO orderVO, PayBankCardVO bankCardVO) {
        String requestId = OrderIdUtil.getAutoRepaymentId();
        String appKey = comFinanceService.getYiBaoSellIdByAgencyId(orderVO.getAgencyId());
        //请求易宝
        YopRequest yopRequest = deductParamWrapper(requestId, appKey, orderVO, bankCardVO);
//        log.info("user:{} request yiBao pay , param:{}", orderVO.getUserId(), JSON.toJSONString(yopRequest));
        Map<String, String> result = YiBaoUtil.request("/rest/v1.0/paperorder/unified/pay", yopRequest);
        log.info("user:{} request yiBao pay the returned result：{}", orderVO.getUserId(), JSON.toJSONString(result));

        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderVO.getOrderNumber());
            lock.lock();
            String status = result.get("status");
            if (status.equals("PROCESSING")) {//处理中
                // 受理成功或者是 支付成功的。 需要等待回调。
                FinanceIOModel ioModel = basicOrderService.initOrder(orderVO, bankCardVO, requestId, FinanceConstant.PayPlatform.CHANGJIE.code, FinanceConstant.PayType.DEDUCT);

                basicOrderService.dealOrderSuccess(ioModel);

                //成功创建订单代扣流水表
                return PayResultVO.builder().sts(CommonConst.YES).msg("提交成功")
                        .orderNumber(orderVO.getOrderNumber()).payTrxId(requestId).build();
            } else {
                // 如果是失败的，需要看看订单的状态
                String errorCode = result.get("errorcode");
                String errorMsg = result.get("errormsg");
                log.info("yibao request pay fail,errorCode={},msg={},param={}", errorCode, errorMsg, JSON.toJSONString(yopRequest));

                if (YibaoConstant.commonErrorList.contains(errorCode)) {
                    // 这几个状态的情况下，可以进行下一次扣款操作。
                    return PayResultVO.builder().sts(CommonConst.OK).msg(errorMsg)
                            .orderNumber(orderVO.getOrderNumber()).payTrxId(requestId)
                            .build();
                } else {
                    return PayResultVO.builder().sts(CommonConst.NO).msg(errorMsg)
                            .orderNumber(orderVO.getOrderNumber()).payTrxId(requestId)
                            .build();
                }

            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    private YopRequest deductParamWrapper(String requestId, String appKey, PayOrderVO orderVO, PayBankCardVO bankCardVO) {
        YopRequest yopRequest = new YopRequest(appKey);
        yopRequest.addParam("merchantno", appKey.substring(4));
        yopRequest.addParam("requestno", requestId);
        yopRequest.addParam("issms", false);
        yopRequest.addParam("identityid", bankCardVO.getUserThridId());
        yopRequest.addParam("identitytype", "USER_ID");
        yopRequest.addParam("cardtop", bankCardVO.getBankCardNum().substring(0, 6));
        yopRequest.addParam("cardlast", bankCardVO.getBankCardNum().substring(bankCardVO.getBankCardNum().length() - 4));
        yopRequest.addParam("amount", FormatUtil.formatDouble2(orderVO.getPayment()));
        yopRequest.addParam("terminalno", "SQKKSCENEKJ010");
        //回调地址
        yopRequest.addParam("callbackurl", springProperties.getString("yiBao.rePayNotifyUrl") + comFinanceService.getAgencyMd5ById(orderVO.getAgencyId()) + ".do");
        yopRequest.addParam("requesttime", SimpleDateFormatUtil.dateToString(new Date(), SimpleDateFormatUtil.DATE_FORMAT6));
        yopRequest.addParam("productname", "自动还款");
        yopRequest.addParam("avaliabletime", 30);

        return yopRequest;
    }

    @Override
    public String getCode() {
        return FinanceConstant.PayPlatform.YIBAO.code;
    }

    private YopRequest payParam(UserModel userModel, UserBankCardModel userBankCardModel, Double money, UserInfoModel infoModel, String bankCardNum, String requestId, String appKey) {
        YopRequest yopRequest = new YopRequest(appKey);
        yopRequest.addParam("merchantno", appKey.substring(4));
        yopRequest.addParam("requestno", requestId);
        yopRequest.addParam("identityid", userModel.getThirdId());
        yopRequest.addParam("identitytype", "USER_ID");
        yopRequest.addParam("cardno", bankCardNum);
        yopRequest.addParam("idcardno", infoModel.getIdcardNum());
        yopRequest.addParam("idcardtype", "ID");
        yopRequest.addParam("username", infoModel.getName());
        yopRequest.addParam("phone", userBankCardModel.getBankPhone());
        yopRequest.addParam("amount", FormatUtil.formatDouble2(money));
        yopRequest.addParam("authtype", "COMMON_FOUR");
        yopRequest.addParam("issms", "true");
        yopRequest.addParam("avaliabletime", "10");
        yopRequest.addParam("callbackurl", springProperties.getString("yiBao.rePayNotifyUrl") + comFinanceService.getAgencyMd5ById(userModel.getAgencyId()) + ".do");
        yopRequest.addParam("requesttime", SimpleDateFormatUtil.dateToString(new Date(), SimpleDateFormatUtil.DATE_FORMAT6));
        yopRequest.addParam("terminalno", "SQKKSCENEKJ010");
        return yopRequest;
    }

    private FinanceIOModel getAndCheckIoModel(String requestId) {
        FinanceIOModel ioModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
        if (ioModel == null) {
            throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
        }
        return ioModel;
    }

}