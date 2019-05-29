//package org.songbai.loan.risk.mould.event;
//
//import lombok.Builder;
//import lombok.Getter;
//import org.songbai.loan.risk.vo.OverMouldVO;
//import org.songbai.loan.risk.vo.VariableExtractVO;
//import org.songbai.loan.risk.vo.VariableMergeVO;
//
//
//@Builder
//@Getter
//public class RiskEvent {
//
//    public static final int TYPE_EXTRACT = 1;
//    public static final int TYPE_MERGE = 2;
//    public static final int TYPE_MOULD_CATALOG = 3;
//    public static final int TYPE_MOULD_ALL = 4;
//
//    private Integer type; // 1 抽取，2合并，3,分类过模型 ，4 所有过模型
//
//
//    private VariableMergeVO mergeVO;
//    private OverMouldVO mouldVO;
//    private VariableExtractVO extractVO;
//
//
//    public static RiskEvent ofExtract(VariableExtractVO extractVO) {
//
//        return RiskEvent.builder().type(TYPE_EXTRACT).extractVO(extractVO).build();
//    }
//
//
//    public static RiskEvent ofMerge(VariableMergeVO mergeVO) {
//
//        return RiskEvent.builder().type(TYPE_MERGE).mergeVO(mergeVO).build();
//    }
//
//
//    public static RiskEvent ofMouldCatalog(OverMouldVO mouldVO) {
//
//        return RiskEvent.builder().type(TYPE_MOULD_CATALOG).mouldVO(mouldVO).build();
//    }
//
//
//    public static RiskEvent ofMouldAll(OverMouldVO mouldVO) {
//
//        return RiskEvent.builder().type(TYPE_MOULD_ALL).mouldVO(mouldVO).build();
//    }
//}
