//package org.songbai.loan.risk.mould.helper;
//
//
//import org.songbai.loan.risk.mould.event.MouldEventBus;
//import org.songbai.loan.risk.mould.event.RiskEvent;
//import org.songbai.loan.risk.vo.OverMouldVO;
//import org.songbai.loan.risk.vo.VariableMergeVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RiskNotifyHelper {
//
////
////    @Autowired
////    JmsTemplate jmsTemplate;
//
//    @Autowired
//    MouldEventBus mouldEventBus;
//
//    /**
//     * 变量抽取完成,通知合并变量。
//     *
//     * @param resultVO
//     */
//    public void notifyRiskVariableMerge(VariableMergeVO resultVO) {
//
////
////        jmsTemplate.convertAndSend(RiskJmsDest.RISK_VARIABLE_MERGE, resultVO);
//        mouldEventBus.post(RiskEvent.ofMerge(resultVO));
//    }
//
//
//    /**
//     * 已经完成变量合并， 通知过风控模型
//     *
//     * @param userId
//     * @param catalog
//     */
//    public void notifyRiskVariableOverMould(String userId, Integer catalog) {
//
//        OverMouldVO mouldVO = OverMouldVO.builder().catalog(catalog).userId(userId).build();
////
////
////        jmsTemplate.convertAndSend(RiskJmsDest.RISK_VARIABLE_OVERMOULD, mouldVO);
//
//        mouldEventBus.post(RiskEvent.ofMouldCatalog(mouldVO));
//    }
//
//
//    /**
//     * 已经完成变量合并， 通知过风控模型
//     *
//     * @param userId
//     */
//    public void notifyOrderOverMould(String userId) {
//
//        OverMouldVO mouldVO = OverMouldVO.builder().userId(userId).build();
////
////
////        jmsTemplate.convertAndSend(RiskJmsDest.RISK_VARIABLE_OVERMOULD, mouldVO);
//
//        mouldEventBus.post(RiskEvent.ofMouldAll(mouldVO));
//    }
//
//
//}
