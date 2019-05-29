package org.songbai.loan.user.finance.service;

import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.JhPayModel;
import org.springframework.stereotype.Component;

/**
 * 聚合支付宝与微信支付
 */
@Component
public interface JhPayService {

    /**
     * 聚合支付
     */
    Ret pay(String orderNum, String payCode, Integer userId);

    /**
     * 回调通知
     */
    void jhPayNotify(JhPayModel jhPayModel);

    /**
     * 订单交易确认
     */
    String dealJhOrder(FinanceIOModel ioModel);

    /**
     * 二维码支付
     */
    Ret scanPay(String orderNum, String payCode, Integer userId);
}
