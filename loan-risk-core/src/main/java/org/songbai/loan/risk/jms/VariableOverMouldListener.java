//package org.songbai.loan.risk.jms;
//
//import lombok.extern.slf4j.Slf4j;
//import org.songbai.loan.util.risk.RiskJmsDest;
//import org.songbai.loan.risk.vo.OverMouldVO;
//import org.songbai.loan.risk.mould.variable.CatalogCalc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//
//@Component
//@Slf4j
//public class VariableOverMouldListener {
//
//
//    @Autowired
//    CatalogCalc mouldOverCalc;
//
//
//    /**
//     * 过风控模型
//     *
//     * @param mouldVO
//     */
//    @JmsListener(destination = RiskJmsDest.RISK_VARIABLE_OVERMOULD)
//    public void variableOverMould(OverMouldVO mouldVO) {
//
//
//        log.info("receive msg for {} , and start overmould", mouldVO);
//
//        try {
//            mouldOverCalc.calc(mouldVO);
//        } catch (Exception e) {
//            log.error("计算风控模型失败，{}", mouldVO);
//        }
//
//
//        log.info("handle msg: {} , and end overmould", mouldVO);
//
//
//    }
//
//
//}
