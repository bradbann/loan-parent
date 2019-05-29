//package org.songbai.loan.risk.mould.variable.over;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.songbai.loan.model.user.UserInfoModel;
//import org.songbai.loan.risk.jms.RiskJmsNotifyHelper;
//import org.songbai.loan.risk.model.mould.RiskMouldModel;
//import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
//import org.songbai.loan.risk.mould.helper.RiskResultBuilder;
//import org.songbai.loan.risk.service.RiskMouldService;
//import org.songbai.loan.risk.service.RiskOrderService;
//import org.songbai.loan.risk.vo.RiskResultVO;
//import org.songbai.loan.service.user.service.ComUserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class MouldCalcHelper {
//
//    MongoTemplate mongoTemplate;
//
//    @Autowired
//    RiskMouldService riskMouldService;
//
//    public RiskResultVO calc(String thirdId, String orderNumber) {
//        UserInfoModel infoModel = userService.selectUserInfoByThridId(thirdId);
//        RiskMouldModel mouldModel = riskMouldService.selectRiskMouldModel();
//
//        List<RiskUserMouldCatalogModel> mouldCatalogModels = getVarResultModels(thirdId);
//
//
//        if (!isAllReady(mouldCatalogModels, mouldModel.getId())) {
//            RiskResultVO resultVO = RiskResultVO.builder()
//                    .code(3).msg("数据没有准备完成")
//                    .userId(thirdId)
//                    .orderNumber(orderNumber)
//                    .build();
//
//
//            return resultVO;
//        }
//
//        RiskResultBuilder builder = RiskResultBuilder.create(mouldModel, infoModel.getSex());
//
//
//        builder.calc(mouldCatalogModels);
//
//        RiskResultVO resultVO = builder.build();
//        resultVO.setUserId(thirdId);
//        resultVO.setOrderNumber(orderNumber);
//        return resultVO;
//    }
//
//
//    private List<RiskUserMouldCatalogModel> getVarResultModels(String thirdId) {
//        Query query = Query.query(Criteria.where("userId").is(thirdId));
//
//
//        return mongoTemplate.find(query, RiskUserMouldCatalogModel.class);
//    }
//
//
//    private boolean isAllReady(List<RiskUserMouldCatalogModel> list, Integer riskMouldId) {
//        List<Integer> catalogList1 = list.stream().map(RiskUserMouldCatalogModel::getCatalog).collect(Collectors.toList());
//
//        List<Integer> catalogList2 = riskMouldService.selectMouldVariableCatalog(riskMouldId);
//
//
//        if (catalogList1.size() != catalogList2.size()) {
//
//            return false;
//        }
//
//
//        return CollectionUtils.isEqualCollection(catalogList1, catalogList2);
//    }
//
//}
