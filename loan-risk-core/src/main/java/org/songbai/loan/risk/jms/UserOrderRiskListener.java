package org.songbai.loan.risk.jms;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.risk.service.RiskOrderService;
import org.songbai.loan.risk.service.UserMouldService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static org.songbai.loan.constant.JmsDest.RISK_ORDER_MOULD;


@Component
@Slf4j
public class UserOrderRiskListener {


    @Autowired
    UserMouldService userMouldService;

    @Autowired
    ComUserService userService;

    @Autowired
    RiskOrderService riskOrderService;


    @JmsListener(destination = RISK_ORDER_MOULD)
    public void userOrder(RiskOrderVO vo) {
        try {
            if (vo == null) {
                return;
            }

            UserRiskOrderModel riskOrderModel = riskOrderService.selectRiskOrderModel(vo.getThridId(), vo.getOrderNumber());

            if (riskOrderModel == null) {
                riskOrderService.submitOrder(vo.getThridId(), vo.getOrderNumber());
            }

            userMouldService.calcAsyncMulti(vo);
        } catch (Exception e) {
            log.error("风控订单失败，参数" + vo.toString(), e);
        }

    }

}
