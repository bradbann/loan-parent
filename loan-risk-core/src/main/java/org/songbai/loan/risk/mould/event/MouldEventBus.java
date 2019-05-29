//package org.songbai.loan.risk.mould.event;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.songbai.cloud.basics.concurrent.Executors;
//import org.songbai.loan.risk.mould.variable.MergeCalc;
//import org.songbai.loan.risk.vo.VariableMergeVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.concurrent.ExecutorService;
//
//
//@Component
//@Slf4j
//public class MouldEventBus {
//
//    private ExecutorService executorService;
//
//    @Autowired
//    private MergeCalc mergeCalc;
////    @Autowired
////    CatalogCalc catalogCalc;
//
//
//    @PostConstruct
//    public void init() {
//        executorService = Executors.newFixedThreadPool(4, 10, "mould-risk-event");
//    }
//
//
//    public void post(RiskEvent event) {
//        executorService.submit(() -> {
//
//            switch (event.getType()) {
//                case RiskEvent.TYPE_EXTRACT:
//                    return;
//                case RiskEvent.TYPE_MERGE:
//                    variableMerge(event.getMergeVO());
//                    return;
//                case RiskEvent.TYPE_MOULD_CATALOG:
////                    variableOverMould(event.getMouldVO());
//                    return;
//                case RiskEvent.TYPE_MOULD_ALL:
////                    variableOverMouldALL(event.getMouldVO());
//                    return;
//            }
//
//
//        });
//    }
//
//
//    /**
//     * 变量合并
//     */
//    private void variableMerge(VariableMergeVO resultVO) {
//        log.info("receive msg for {} , and start merge", resultVO);
//        try {
//            mergeCalc.merge(resultVO);
//        } catch (Exception e) {
//            log.error("merge variable error ", e);
//        }
//        log.info("handle msg: {} , and end merge variable", resultVO);
//    }
//
//
////    private void variableOverMould(OverMouldVO mouldVO) {
////        log.info("receive msg for {} , and start overmould", mouldVO);
////        try {
////            catalogCalc.calc(mouldVO);
////        } catch (Exception e) {
////            log.error("计算风控模型失败，{}", mouldVO);
////        }
////        log.info("handle msg: {} , and end overmould", mouldVO);
////    }
//
//
////    private void variableOverMouldALL(OverMouldVO mouldVO) {
////        log.info("receive msg for {} , and start overmould", mouldVO);
////        try {
////            catalogCalc.calc(mouldVO);
////        } catch (Exception e) {
////            log.error("计算风控模型失败，{}", mouldVO);
////        }
////        log.info("handle msg: {} , and end overmould", mouldVO);
////    }
//
//
//    @PreDestroy
//    public void Destroy() {
//
//        if (executorService != null) {
//            executorService.shutdown();
//        }
//    }
//
//}
