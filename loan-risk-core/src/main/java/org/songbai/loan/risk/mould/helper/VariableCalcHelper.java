package org.songbai.loan.risk.mould.helper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.dao.RiskVariableDao;
import org.songbai.loan.risk.model.mould.RiskVariableSourceModel;
import org.songbai.loan.risk.model.user.RiskUserVarTempModel;
import org.songbai.loan.risk.mould.express.Context;
import org.songbai.loan.risk.mould.express.Express;
import org.songbai.loan.risk.mould.mongo.RiskUserVarTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Component
@Slf4j
public class VariableCalcHelper {

    @Autowired
    RiskVariableDao riskVariableDao;


    @Autowired
    RiskUserVarTempRepository tempRepository;


    public List<RiskVariableSourceModel> getSourceModels(String sources) {
        List<RiskVariableSourceModel> sourceModels = riskVariableDao.selectVariableSource(sources);


        if (sourceModels == null || sourceModels.isEmpty()) {
            log.error("不能找到 {} 的变量配置,{}", sources);
            throw new RuntimeException("不能找到 " + sources + " 的变量配置");
        }

        return sourceModels;
    }


    public List<RiskUserVarTempModel> calcVariable(List<RiskVariableSourceModel> sourceModels, Context context) {
        List<RiskUserVarTempModel> resultList = new ArrayList<>();
        for (RiskVariableSourceModel sourceModel : sourceModels) {
            String result = Express.calcStr(sourceModel, context);

            RiskUserVarTempModel model = new RiskUserVarTempModel();

            model.setCatalog(sourceModel.getCatalog());
            model.setVariableKey(sourceModel.getVariableCode());
            model.setVariableValue(result);

            resultList.add(model);
        }

        return resultList;
    }


    public void saveVariableTempModel(String userId, String source, List<RiskUserVarTempModel> tempModels) {

        for (RiskUserVarTempModel tempModel : tempModels) {
            tempModel.setUserId(userId);
            tempModel.setSources(source);
            tempModel.setCreateTime(new Date());
        }

        tempRepository.insert(tempModels);
    }


}
