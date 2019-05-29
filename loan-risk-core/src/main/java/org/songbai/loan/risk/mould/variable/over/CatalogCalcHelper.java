package org.songbai.loan.risk.mould.variable.over;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
import org.songbai.loan.risk.model.user.RiskUserVarResultModel;
import org.songbai.loan.risk.mould.express.SymbolCalc;
import org.songbai.loan.risk.mould.helper.UserCatalogBuilder;
import org.songbai.loan.risk.service.RiskMouldService;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * @author navy
 */
@Slf4j
public class CatalogCalcHelper {


    private RiskMouldService riskMouldDao;

    private MongoTemplate mongoTemplate;
    private RiskMouldModel mouldModel;


    CatalogCalcHelper(MongoTemplate mongoTemplate, RiskMouldService riskMouldService, RiskMouldModel mouldModel) {
        this.mongoTemplate = mongoTemplate;
        this.riskMouldDao = riskMouldService;
        this.mouldModel = mouldModel;
    }


    public RiskUserMouldCatalogModel calc(RiskMouldWeightModel weightModel, RiskOrderVO orderVO) {

        JSONObject jsonObject = getVariableParam(orderVO.getThridId(), weightModel.getCatalog());


        HashMap<String, Entry> entryHashMap = getVariableGroupByCode(weightModel.getCatalog());

        UserCatalogBuilder mouldBuilder = createUserMouldBuilder(jsonObject, entryHashMap);

        RiskUserMouldCatalogModel userMouldModel = mouldBuilder.weight(weightModel).build();

        userMouldModel.setUserId(orderVO.getThridId());
        userMouldModel.setOrderNumber(orderVO.getOrderNumber());
        userMouldModel.setCatalog(weightModel.getCatalog());

        Criteria criteria = Criteria.where("userId").is(orderVO.getThridId())
                .and("catalog").is(weightModel.getCatalog())
                .and("orderNumber").is(orderVO.getOrderNumber());

        mongoTemplate.remove(Query.query(criteria), RiskUserMouldCatalogModel.class);
        mongoTemplate.save(userMouldModel);

        return userMouldModel;
    }

    private UserCatalogBuilder createUserMouldBuilder(JSONObject jsonObject, HashMap<String, Entry> entryHashMap) {
        UserCatalogBuilder mouldBuilder = UserCatalogBuilder.create(mouldModel).variableParam(jsonObject);

        for (Entry value : entryHashMap.values()) {

            String param = jsonObject.getString(value.getVariableCode());

            RiskMouldVariableModel temp = hitVarSection(value.getList(), param);

            if (temp != null) {
                mouldBuilder.hitVariable(temp, param)
                        .riskLevel(temp.getRiskLevel())
                        .oper(temp.getOperType(), temp.getOperScore());
            } else {

                if (value.getList().size() > 0) {
                    temp = new RiskMouldVariableModel();

                    temp.setMouldId(value.getList().get(0).getMouldId());
                    temp.setCatalog(value.getList().get(0).getCatalog());
                    temp.setVariableCode(value.getList().get(0).getVariableCode());
                    temp.setVariableName(value.getList().get(0).getVariableName());
                    temp.setStatus(value.getList().get(0).getStatus());
                    temp.setIndexed(value.getList().get(0).getIndexed());

                    mouldBuilder.hitVariable(temp, param);
                }
            }
        }


        return mouldBuilder;
    }

    private HashMap<String, Entry> getVariableGroupByCode(Integer catalog) {
        List<RiskMouldVariableModel> mouldVarList = riskMouldDao.selectMouldVarList(catalog, mouldModel.getId());

        if (mouldVarList == null) {
            log.error("不能找到模型变量配置:{}", catalog);
            throw new RuntimeException("不能找到模型变量配置:" + mouldModel.getId());
        }

        HashMap<String, Entry> entryHashMap = new HashMap<>();
        for (RiskMouldVariableModel variableModel : mouldVarList) {

            entryHashMap.computeIfAbsent(variableModel.getVariableCode(), Entry::new)
                    .addVariableModel(variableModel);
        }
        return entryHashMap;
    }


    private JSONObject getVariableParam(String userId, Integer catalog) {
        RiskUserVarResultModel resultModel = getRiskUserVarResultModel(userId, catalog);

        if (resultModel == null) {
            log.error("不能读取到抽取的变量：user:{},catalog:{}", userId, catalog);
            throw new RuntimeException("不能读取到抽取的变量 user:" + userId + "catalog:" + catalog);
        }

        return resultModel.getData();
    }


    private RiskMouldVariableModel hitVarSection(Collection<RiskMouldVariableModel> variableModels, String param) {
        for (RiskMouldVariableModel model : variableModels) {

            boolean is = SymbolCalc.calc(model.getCalcSymbol(), param, model.getCalcLeft(), model.getCalcRight());
            if (is) {
                return model;
            }
        }

        return null;
    }

    private RiskUserVarResultModel getRiskUserVarResultModel(String userId, Integer catalog) {
        Criteria criteria = Criteria.where("userId").is(userId).and("catalog").is(catalog);

        return mongoTemplate.findOne(Query.query(criteria), RiskUserVarResultModel.class);
    }

    public class Entry {

        String variableCode;

        List<RiskMouldVariableModel> list = new ArrayList<>();

        Entry(String variableCode) {
            this.variableCode = variableCode;
        }

        String getVariableCode() {
            return variableCode;
        }

        List<RiskMouldVariableModel> getList() {
            return list;
        }

        void addVariableModel(RiskMouldVariableModel mouldVariableModel) {
            this.list.add(mouldVariableModel);
        }
    }


}
