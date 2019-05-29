package org.songbai.loan.user.finance.controller.v2;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.common.helper.LimitRequestHelper;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.user.finance.service.FinanceIOService;
import org.songbai.loan.user.finance.service.JhPayService;
import org.songbai.loan.user.finance.service.impl.FinanceIOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repayment/v2")
@LimitLess
public class RepaymentCtrl {
    private static final Logger logger = LoggerFactory.getLogger(RepaymentCtrl.class);
    @Autowired
    FinanceIOServiceImpl ioService;
    @Autowired
    JhPayService jhPayService;
    @Autowired
    FinanceIOService financeIOService;
    @Autowired
    LimitRequestHelper limitRequestHelper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/pay")
    public Response pay(String orderNumber, String payCode, Integer userId) {
        checkBasePayParam(orderNumber, payCode, userId);

//        Integer userId = 10;
        try {
            limitRequestHelper.validateUserRequest(UserRedisKey.LIMIT_USER_REPAYMENT, userId, 3L);
            return Response.success(jhPayService.pay(orderNumber, payCode, userId));
        } finally {
            redisTemplate.opsForHash().delete(UserRedisKey.LIMIT_USER_REPAYMENT, userId);
        }

    }


    @PostMapping("/scanPay")
    public Response scanPay(String orderNumber, String payCode, Integer userId) {
        checkBasePayParam(orderNumber, payCode, userId);
//        Integer userId = 10;

        try {
            limitRequestHelper.validateUserRequest(UserRedisKey.LIMIT_USER_REPAYMENT, userId, 3L);
            return Response.success(jhPayService.scanPay(orderNumber, payCode, userId));
        } finally {
            redisTemplate.opsForHash().delete(UserRedisKey.LIMIT_USER_REPAYMENT, userId);
        }
    }


    @PostMapping("/dealJhOrder")
    public Response pay(String orderNumber, Integer userId, String requestId) {
        Assert.hasText(orderNumber, "订单号不能为空");
        Ret ret = Ret.create();
        //第三方流水号若不存在，取最后一条数据的流水号
        if (StringUtils.isEmpty(requestId) || StringUtils.equals(requestId, "undefined")) {
            FinanceIOModel ioModel = financeIOService.getLastIoModelByOrderIdUserId(orderNumber, userId);
            if (ioModel == null) {
                ret.put("payResult", "流水号不存在，支付失败");
                return Response.success(ret);
            }
            requestId = ioModel.getRequestId();
        }
        if (userId == null) {
            throw new BusinessException(UserRespCode.USER_NOT_EXIST);
        }
//        Integer userId = 10;


        FinanceIOModel ioModel = financeIOService.getIoModelByOrderIdAndRequestId(orderNumber, requestId, userId);
//        FinanceIOModel ioModel = financeIOService.getLastIoModelByOrderIdUserId(orderNum, userId);
        if (ioModel == null) {
            logger.info("jhPayService dealOrder ioModel is not exist,orderNumber={},userId={}", orderNumber, userId);
            ret.put("payResult", "订单不存在,支付失败");
            return Response.success(ret);
        }
        ret.put("payResult", jhPayService.dealJhOrder(ioModel));
        return Response.success(ret);
    }

    private void checkBasePayParam(String orderNum, String payCode, Integer userId) {
        Assert.hasText(orderNum, "订单号不能为空");
        Assert.hasText(payCode, "支付类型不能为空");
        if (!payCode.equals(FinanceConstant.PayPlatform.ALIPAYY.code) && !payCode.equals(FinanceConstant.PayPlatform.WXPAY.code)) {
            throw new BusinessException(UserRespCode.REQUEST_PAY_FAILED);
        }
        if (userId == null) {
            throw new BusinessException(UserRespCode.USER_NOT_EXIST);
        }
    }
}
