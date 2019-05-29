//package org.songbai.loan.risk.mould.variable.over;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.songbai.loan.risk.dao.RiskMouldDao;
//import org.songbai.loan.risk.model.mould.RiskMouldModel;
//import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
//import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
//import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
//import org.songbai.loan.risk.model.user.RiskUserVarResultModel;
//import org.songbai.loan.risk.mould.express.SymbolCalc;
//import org.songbai.loan.risk.mould.helper.RiskNotifyHelper;
//import org.songbai.loan.risk.mould.helper.UserCatalogBuilder;
//import org.songbai.loan.risk.mould.variable.CatalogCalc;
//import org.songbai.loan.risk.vo.OverMouldVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//
//
///**
// * @author navy
// */
//@Service
//@Slf4j
//public class CatalogCalcImpl implements CatalogCalc {
//
//
//    @Autowired
//    private RiskMouldDao riskMouldDao;
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @Autowired
//    private RiskNotifyHelper riskNotifyHelper;
//
//    @Override
//    public RiskUserMouldCatalogModel calc(OverMouldVO mouldVO) {
//
//        JSONObject jsonObject = getVariableParam(mouldVO);
//
//
//        RiskMouldModel mouldModel = getRiskMouldModel(mouldVO);
//
//
//        HashMap<String, Entry> entryHashMap = getVariableGroupByCode(mouldVO, mouldModel);
//
//        UserCatalogBuilder mouldBuilder = getUserMouldBuilder(jsonObject, mouldModel, entryHashMap);
//
//
//        RiskMouldWeightModel weightModel = riskMouldDao.selectMouldWeightModel(mouldModel.getId(), mouldVO.getCatalog());
//
//
//        if (weightModel == null) {
//            throw new RuntimeException("不能找到模型分组权重配置:" + mouldModel.getId() + " ," + mouldVO.getCatalog());
//        }
//
//
//        RiskUserMouldCatalogModel userMouldModel = mouldBuilder.weight(weightModel).build();
//
//        userMouldModel.setUserId(mouldVO.getUserId());
//        userMouldModel.setCatalog(mouldVO.getCatalog());
//
//        mongoTemplate.save(userMouldModel);
//
//        riskNotifyHelper.notifyOrderOverMould(mouldVO.getUserId());
//
//        return userMouldModel;
//    }
//
//    private UserCatalogBuilder getUserMouldBuilder(JSONObject jsonObject, RiskMouldModel mouldModel, HashMap<String, Entry> entryHashMap) {
//        UserCatalogBuilder mouldBuilder = UserCatalogBuilder.create(mouldModel);
//
//
//        for (Entry value : entryHashMap.values()) {
//
//            String param = jsonObject.getString(value.getVariableCode());
//
//            RiskMouldVariableModel temp = hitVarSection(value.getList(), param);
//
//            if (temp == null) {
//                continue;
//            }
//
//            mouldBuilder.riskLevel(temp.getRiskLevel())
//                    .oper(temp.getOperType(), temp.getOperScore());
//        }
//
//
//        return mouldBuilder;
//    }
//
//    private HashMap<String, Entry> getVariableGroupByCode(OverMouldVO mouldVO, RiskMouldModel mouldModel) {
//        List<RiskMouldVariableModel> mouldVarList = riskMouldDao.selectMouldVarList(mouldVO.getCatalog(), mouldModel.getId());
//
//        if (mouldVarList == null) {
//            log.error("不能找到模型变量配置:{}", mouldVO);
//            throw new RuntimeException("不能找到模型变量配置:" + mouldModel.getId());
//        }
//
//        HashMap<String, Entry> entryHashMap = new HashMap<>();
//        for (RiskMouldVariableModel variableModel : mouldVarList) {
//
//            entryHashMap.computeIfAbsent(variableModel.getVariableCode(), Entry::new)
//                    .addVariableModel(variableModel);
//        }
//        return entryHashMap;
//    }
//
//    private RiskMouldModel getRiskMouldModel(OverMouldVO mouldVO) {
//        RiskMouldModel mouldModel = riskMouldDao.selectRiskMouldModel();
//
//        if (mouldModel == null) {
//            log.error("不能查询到启用的风控模型:{}", mouldVO);
//            throw new RuntimeException("不能查询到启用的风控模型" + mouldVO);
//        }
//        return mouldModel;
//    }
//
//    private JSONObject getVariableParam(OverMouldVO mouldVO) {
//        RiskUserVarResultModel resultModel = getRiskUserVarResultModel(mouldVO);
//
//        if (resultModel == null) {
//            log.error("不能读取到抽取的变量：{}", mouldVO);
//            throw new RuntimeException("不能读取到抽取的变量" + mouldVO);
//        }
//
//        return resultModel.getData();
//
////        String resultData = resultModel.getData();
////        return JSONObject.parseObject(resultData);
//    }
//
//
//    private RiskMouldVariableModel hitVarSection(Collection<RiskMouldVariableModel> variableModels, String param) {
//        for (RiskMouldVariableModel model : variableModels) {
//
//            boolean is = SymbolCalc.calc(model.getCalcSymbol(), param, model.getCalcLeft(), model.getCalcRight());
//            if (is) {
//                return model;
//            }
//        }
//
//        return null;
//    }
//
//    private RiskUserVarResultModel getRiskUserVarResultModel(OverMouldVO mouldVO) {
//        Criteria criteria = Criteria.where("userId").is(mouldVO.getUserId()).and("catalog").is(mouldVO.getCatalog());
//
//        return mongoTemplate.findOne(Query.query(criteria), RiskUserVarResultModel.class);
//    }
//
//    public class Entry {
//
//        String variableCode;
//
//        List<RiskMouldVariableModel> list = new ArrayList<>();
//
//        Entry(String variableCode) {
//            this.variableCode = variableCode;
//        }
//
//        String getVariableCode() {
//            return variableCode;
//        }
//
//        List<RiskMouldVariableModel> getList() {
//            return list;
//        }
//
//        void addVariableModel(RiskMouldVariableModel mouldVariableModel) {
//            this.list.add(mouldVariableModel);
//        }
//    }
//
//
//}
