//package org.songbai.loan.risk.jms;
//
//import lombok.extern.slf4j.Slf4j;
//import org.songbai.loan.util.risk.RiskJmsDest;
//import org.songbai.loan.risk.mould.variable.MergeCalc;
//import org.songbai.loan.risk.vo.VariableMergeVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//
//@Slf4j
//@Component
//public class VariableMergeListener {
//
//
//    @Autowired
//    private MergeCalc mergeCalc;
//
//
//    /**
//     * 变量合并
//     */
//    @JmsListener(destination = RiskJmsDest.RISK_VARIABLE_MERGE)
//    public void variableMerge(VariableMergeVO resultVO) {
//
//        log.info("receive msg for {} , and start merge", resultVO);
//
//
//        try {
//            mergeCalc.merge(resultVO.getUserId(), resultVO.getCatalogs());
//        } catch (Exception e) {
//            log.error("merge variable error ", e);
//        }
//
//
//        log.info("handle msg: {} , and end merge variable", resultVO);
//
//
//    }
//
//
//}
