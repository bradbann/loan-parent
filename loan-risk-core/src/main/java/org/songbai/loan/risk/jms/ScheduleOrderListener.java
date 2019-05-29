package org.songbai.loan.risk.jms;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.risk.service.RiskOrderService;
import org.songbai.loan.risk.service.UserMouldService;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.songbai.loan.constant.JmsDest.SCHEDULE_ORDER_WAITDATA;

@Component
@Slf4j
public class ScheduleOrderListener {


    @Autowired
    RiskOrderService riskOrderService;

    @Autowired
    UserMouldService userMouldService;


    @Autowired
    JmsTemplate jmsTemplate;


    @Autowired
    RiskJmsNotifyHelper notifyHelper;


    @JmsListener(destination = SCHEDULE_ORDER_WAITDATA)
    public void handlerOrder(JSONObject jsonObject) {

        log.info("receive msg and handle waitedate for :" + jsonObject);

        List<UserRiskOrderModel> list = riskOrderService.selectUserRiskOrderModel();

        for (UserRiskOrderModel model : list) {

            long overtime = System.currentTimeMillis() - model.getCreateTime().getTime();

            if (overtime > 12 * 60 * 60 * 1000) {

                RiskResultVO resultVO = RiskResultVO.builder()
                        .code(RiskResultVO.CODE_SUCESS)
                        .msg("等待数据超时")
                        .riskResult(RiskConst.Result.REJECT.code)
                        .riskResultMsg(Arrays.asList("等待数据超时"))
                        .userId(model.getUserId())
                        .orderNumber(model.getOrderNumber())
                        .mouldId(model.getMouldId())
                        .build();

                notifyHelper.notifyOrderRiskResult(resultVO);

                riskOrderService.authFail(resultVO);

                continue;
            }


            userMouldService.calcAsyncMulti(
                    RiskOrderVO.builder()
                            .orderNumber(model.getOrderNumber())
                            .thridId(model.getUserId()).build());
        }


    }

}
