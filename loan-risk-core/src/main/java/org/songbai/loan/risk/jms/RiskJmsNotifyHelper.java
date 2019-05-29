package org.songbai.loan.risk.jms;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RiskJmsNotifyHelper {


    @Autowired
    JmsTemplate jmsTemplate;


    public void notifyOrderRiskResult(RiskResultVO resultVO) {
        if (resultVO.getCode() == RiskResultVO.CODE_SUCESS) {

            RiskOrderResultVO orderResultVO = new RiskOrderResultVO();

            orderResultVO.setUserId(resultVO.getUserId());
            orderResultVO.setOrderNumber(resultVO.getOrderNumber());
            orderResultVO.setResultMsg(StringUtils.join(resultVO.getRiskResultMsg(), ";"));

            if (resultVO.getRiskResult() == RiskConst.Result.MAN.code
                    || resultVO.getRiskResult() == RiskConst.Result.DEFAULT.code) {
                orderResultVO.setResult(RiskConst.Result.DEFAULT.code);
            } else if (resultVO.getRiskResult() == RiskConst.Result.PASS.code) {
                orderResultVO.setResult(RiskConst.Result.PASS.code);
            } else if (resultVO.getRiskResult() == RiskConst.Result.REJECT.code) {
                orderResultVO.setResult(RiskConst.Result.REJECT.code);
            }

            log.info("risk success and notify order service , user:{}, order:{},result:{} ", resultVO.getUserId(), resultVO.getOrderNumber(), JSONObject.toJSONString(resultVO));
            jmsTemplate.convertAndSend(JmsDest.RISK_ORDER_RESULT, orderResultVO);
        } else {
            log.info("risk fail and no notify ,user:{}, order:{},result:{}", resultVO.getUserId(), resultVO.getOrderNumber(), JSONObject.toJSONString(resultVO));
        }


    }


}
