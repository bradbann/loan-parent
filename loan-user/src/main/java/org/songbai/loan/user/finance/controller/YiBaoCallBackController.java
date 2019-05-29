package org.songbai.loan.user.finance.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.PayNotifyService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 易宝回调处理
 */
@RestController
@RequestMapping("/repayment")
@LimitLess
public class YiBaoCallBackController {

    private static final Logger log = LoggerFactory.getLogger(YiBaoCallBackController.class);

    @Autowired
    private BasicOrderService basicOrderService;
    @Autowired
    private FinanceIODao ioDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private ComFinanceService comFinanceService;
    @Autowired
    PayNotifyService payNotifyService;

    @RequestMapping("/yiBaoPayNotify/{agencyIdMD5}")
    public void yiBaoNotify(@PathVariable("agencyIdMD5") String agencyIdMD5, HttpServletRequest request, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String resultResponse = request.getParameter("response");
            if (StringUtils.isBlank(resultResponse)) {
                log.info("【易宝打款】回调结果为NULL");
                writer.write("FAILED");
                return;
            }

            Integer agencyId = comFinanceService.getAgencyIdByMD5(agencyIdMD5);
            String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
            Map<String, String> result = YiBaoUtil.Decrypt(resultResponse, appKey);
            if (MapUtils.isEmpty(result)) {
                log.error("代理：{}配置的私钥和公钥有问题，不能解密回调：{}", agencyId, resultResponse);
                writer.write("FAILED");
                return;
            }

            log.info("【易宝还款】回调结果为：{}", result);
            String requestId = result.get("requestno");
            if (StringUtils.isBlank(requestId)) {
                writer.write("SUCCESS");
                return;
            }
            FinanceIOModel ioModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
            if (getAndCheckOrderModel(writer, requestId, result.get("amount"), ioModel)) return;
            ioModel.setThirdOrderId(result.get("yborderid"));
            DistributeLock lock = null;
            try {
                lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + ioModel.getOrderId());
                lock.lock();
                if (!result.get("status").equals("PAY_SUCCESS")) {
                    log.info("易宝支付回调的订单号：{}还款失败", ioModel.getOrderId());
                    String msg = result.get("errormsg");
                    payNotifyService.payFail(ioModel, msg == null ? "易宝支付交易失败，请确认用户是否退订短信" : msg);
//                    basicOrderService.dealOrderFailed(ioModel, "易宝支付交易失败，请确认用户是否退订短信", true);
                    writer.write("SUCCESS");
                    return;
                }
                log.info("易宝支付回调的用户的：{}订单号：{}还款成功", ioModel.getUserId(), ioModel.getOrderId());
//                basicOrderService.repaymentSuccess(ioModel);
                payNotifyService.paySuccess(ioModel);
                writer.write("SUCCESS");
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }

        }
    }

    private boolean getAndCheckOrderModel(PrintWriter writer, String requestId, String amount, FinanceIOModel ioModel) {
        if (ioModel == null) {
            log.info("易宝支付回调的订单号：{}找不到,不予处理！", requestId);
            writer.write("SUCCESS");
            return true;
        }
        if (ioModel.getStatus() == FinanceConstant.IoStatus.SUCCESS.key) {
            log.info("易宝支付回调订单{}状态已经成功，不予处理,订单详细信息：{}", ioModel.getOrderId(), JSON.toJSONString(ioModel));
            writer.write("SUCCESS");
            return true;
        }
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
        List<Integer> list = Arrays.asList(OrderConstant.Status.PROCESSING.key, OrderConstant.Status.WAIT.key, OrderConstant.Status.OVERDUE.key, OrderConstant.Status.FAIL.key);
        if (!(orderModel.getStage() == OrderConstant.Stage.REPAYMENT.key && list.contains(orderModel.getStatus()))) {
            log.info("易宝支付回调订单{}不是还款处理范围，该订单当前阶段为：{}，状态为：{},订单详细信息：{}", orderModel.getOrderNumber(), orderModel.getStage(), orderModel.getStatus(), JSON.toJSONString(orderModel));
            writer.write("SUCCESS");
            return true;
        }
        if (Double.compare(ioModel.getMoney(), Double.valueOf(amount)) != 0.0) {
            log.info("易宝支付回调金额{}与订单{}金额{}不符，不予处理！", amount, ioModel.getOrderId(), ioModel.getMoney());
            writer.write("SUCCESS");
            return true;
        }
        return false;
    }

}
