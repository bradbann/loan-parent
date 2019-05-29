package org.songbai.loan.user.finance.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.DeductConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.FinanceDeductFlowModel;
import org.songbai.loan.model.loan.FinanceDeductModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;
import org.songbai.loan.user.finance.service.FinanceDeductService;
import org.songbai.loan.user.finance.service.RepaymentService;
import org.songbai.loan.user.finance.service.impl.RepaymentFactory;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2019/1/3 16:39
 * Description: 代扣Listener
 */
@Component
@Slf4j
public class AutoDeductListener {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private UserBankCardDao bankCardDao;
    @Autowired
    private RepaymentFactory repaymentFactory;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private FinanceDeductService deductService;
    @Autowired
    SpringProperties springProperties;


    @JmsListener(destination = JmsDest.AUTO_DEDUCT)
    public void autoRepayment(String msg) {
        log.info("start deduct for :{}", msg);


        FinanceDeductModel deductModel = null;
        try {
            JSONObject json = JSONObject.parseObject(msg);

            deductModel = deductService.selectDeductModelById(json.getInteger("deductId"));

            if (deductModel.getStatus() != DeductConst.Status.WAIT.code) {
                log.warn("扣款在进行中，或者已经处理结束，不处理:{}", json);
                return;
            }

            deductService.updateDeductStatus(deductModel, DeductConst.Status.DEDUCT.code, "扣款处理中");

            handling(deductModel);
        } catch (Exception e) {
            log.error("自动扣款失败：" + e.getMessage(), e);

            if (deductModel != null) {
                if (e instanceof BusinessException && (((BusinessException) e).getCode() == RespCode.SUCCESS)) {
                    deductService.updateDeductStatus(deductModel, DeductConst.Status.FINISH.code, getString(e));
                } else {
                    deductService.updateDeductStatus(deductModel, DeductConst.Status.FAIL.code, getString(e));
                }
            }


        }

    }

    private void handling(FinanceDeductModel deductModel) {
        UserModel userModel = comUserService.selectUserModelById(deductModel.getUserId());

        PayBankCardVO bankCardVO = payBankCardWrapper(deductModel, userModel);
        PayOrderVO orderVO = payOrderWrapper(deductModel, userModel);


        RepaymentService repaymentService = repaymentFactory.getBeanByCode(bankCardVO.getBindPlatform());

        // 保存扣款信息
        FinanceDeductFlowModel flowModel = deductService.saveDeductFlow(deductModel, orderVO, bankCardVO);

        try {
            PayResultVO resultVO = repaymentService.deductPay(orderVO, bankCardVO);
            // 支付结果
            deductService.updateDeductFlowForPayResult(flowModel, resultVO);

            if (resultVO.getSts() == CommonConst.NO) {
                // 失败不处理
                deductService.updateDeductStatus(deductModel, DeductConst.Status.FAIL.code, resultVO.getMsg());
            } else if (resultVO.getSts() == CommonConst.OK) {
                // 失败尝试下一次扣款
                double[] level = deductService.getDeductLimit(deductModel, flowModel);
                // 表示还需要继续扣款。
                if (level[1] > 0) {
                    deductService.updateDeductStatus(deductModel, DeductConst.Status.WAIT.code, resultVO.getMsg() + "，等待下一次扣款");

                    Map<String, Object> recursionMap = new HashMap<>();
                    recursionMap.put("deductId", deductModel.getId());
                    recursionMap.put("orderNumber", deductModel.getOrderNumber());
                    log.info("订单[{}],{},扣款失败， 但是可以尝试下一次扣款。{}", resultVO.getOrderNumber(), resultVO.getMsg(), recursionMap);
                    jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, JSON.toJSONString(recursionMap));

                } else {
                    deductService.updateDeductStatus(deductModel, DeductConst.Status.FINISH.code, resultVO.getMsg() + "，不进行下一次扣款");
                }
            }
        } catch (Exception e) {
            log.error("自动扣款失败,处理失败：" + e.getMessage(), e);

            PayResultVO resultVO = PayResultVO.builder().sts(CommonConst.NO).msg(getString(e)).build();

            deductService.updateDeductFlowForPayResult(flowModel, resultVO);
            deductService.updateDeductStatus(deductModel, DeductConst.Status.FAIL.code, resultVO.getMsg());
        }
    }


    private PayOrderVO payOrderWrapper(FinanceDeductModel deductModel, UserModel userModel) {

        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(deductModel.getOrderNumber(), deductModel.getUserId());

        // check order status，放置用户在还款的过程中。
        if (orderModel.getStage() != OrderConstant.Stage.REPAYMENT.key) {
            throw new BusinessException(RespCode.SUCCESS, "订单不是出于还款阶段");
        }

        int huankuanri = DateUtils.truncatedCompareTo(orderModel.getRepaymentDate(), new Date(), Calendar.DAY_OF_MONTH);

        if (orderModel.getStatus() != OrderConstant.Status.OVERDUE.key
                && orderModel.getStatus() != OrderConstant.Status.FAIL.key
                && !(orderModel.getStatus() == OrderConstant.Status.WAIT.key && huankuanri == 0)
                ) {
            throw new BusinessException(RespCode.SUCCESS, "订单不是逾期或者当日应该还款的订单：" + deductModel.getOrderNumber());
        }


        PayOrderVO vo = PayOrderVO.builder()
                .orderModel(orderModel)
                .userId(userModel.getId())
                .agencyId(orderModel.getAgencyId())
                .orderId(deductModel.getOrderId())
                .orderNumber(deductModel.getOrderNumber())
                .build();


        double[] levelConfig = getDeductRate(deductModel);

        if (levelConfig[1] <= 0) {
            String msg = levelConfig[0] == -1 ? "[配置错误]" : (levelConfig[0] == -2 ? "[数据错误]" : "");
            throw new BusinessException("订单失败" + msg + "，不能在执行扣款");
        }


        BigDecimal pay = null;
        if (levelConfig[0] == DeductConst.DeductType.RATE.code) {
            pay = BigDecimal.valueOf(levelConfig[1]).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(deductModel.getPayment()));
        } else if (levelConfig[0] == DeductConst.DeductType.FIX.code) {
            pay = BigDecimal.valueOf(levelConfig[1]);
        } else {
            throw new BusinessException("订单失败[计算扣款错误]，不能在执行扣款");
        }


        Double payMoney = pay.setScale(2, RoundingMode.HALF_UP).doubleValue();
        vo.setPayment(payMoney);
        vo.setPayRate(Double.valueOf(levelConfig[1]).intValue());

        return vo;
    }


    /**
     * 获取扣款比例
     *
     * @param deductModel
     * @return
     */
    private double[] getDeductRate(FinanceDeductModel deductModel) {
        Integer count = deductService.queryDeductSuccessCount(deductModel.getId());

        // 如果畅捷扣款次数大于3次，那么不再扣款了
        Integer limit = springProperties.getInteger("user.deduct.changjie.times", 3);
        if (count >= limit) {
            throw new BusinessException(RespCode.SUCCESS, "用户扣款次数已经大于" + limit + "次，不进行扣款了");
        }

        FinanceDeductFlowModel lastDeductFlow = deductService.selectDeductFlowModelByDeductId(deductModel.getId());


        return deductService.getDeductLimit(deductModel, lastDeductFlow);
    }


    private PayBankCardVO payBankCardWrapper(FinanceDeductModel deductModel, UserModel userModel) {

        UserBankCardModel bankCardModel = bankCardDao.getUserBindCard(deductModel.getUserId(), FinanceConstant.BankCardType.DEFAULT.key, FinanceConstant.BankCardStatus.BIND.key);
        if (bankCardModel == null) {
            throw new BusinessException(UserRespCode.PLEASE_AUTH);
        }

        UserInfoModel infoModel = comUserService.findUserInfoByUserId(deductModel.getUserId());

        if (infoModel == null || userModel == null) {
            throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS);
        }


        return PayBankCardVO.builder()
                .name(infoModel.getName())
                .idcardNum(infoModel.getIdcardNum())
                .bankName(bankCardModel.getBankName())
                .bankCode(bankCardModel.getBankCode())
                .bankCardNum(bankCardModel.getBankCardNum())
                .bankCardType(bankCardModel.getBankCardType())
                .bankPhone(bankCardModel.getBankPhone())
                .bindPlatform(bankCardModel.getBindPlatform())
                .userId(userModel.getId())
                .userThridId(userModel.getThirdId())
                .build();
    }


    private String getString(Exception e) {
        String message = e.getMessage();

        if (StringUtil.isEmpty(message)) {
            message = "自动扣款失败:" + e.getClass().getSimpleName();
        } else {
            if (message.length() > 400) {
                message = message.substring(0, 300);
            }
        }
        return message;
    }
}
