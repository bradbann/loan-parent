package org.songbai.loan.risk.mould.variable.merge;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.dao.RiskVariableDao;
import org.songbai.loan.risk.model.user.RiskUserVarResultModel;
import org.songbai.loan.risk.model.user.RiskUserVarTempModel;
import org.songbai.loan.risk.mould.mongo.RiskUserVarTempRepository;
import org.songbai.loan.risk.mould.variable.MergeCalc;
import org.songbai.loan.risk.vo.VariableMergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class MergeCalcImpl implements MergeCalc {
    @Autowired
    private RiskVariableDao riskVariableDao;

    @Autowired
    private RiskUserVarTempRepository tempRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void merge(VariableMergeVO resultVO) {


        for (Integer catalog : resultVO.getCatalogs()) {

            List<String> sourceList = riskVariableDao.selectSourceByCatalog(catalog);

            if (sourceList == null || sourceList.size() == 0) {
                log.error("不能找到{}类型的变量来源 ", catalog);
                continue;
            }

            boolean varFinish = true;

            for (String sources : sourceList) {
                Integer count = tempRepository.countVarTempModel(resultVO.getUserId(), catalog, sources);

                varFinish = varFinish && (count > 0);
            }

            if (varFinish) {
                mergeCatalog(resultVO.getUserId(), catalog);
//                riskNotifyHelper.notifyRiskVariableOverMould(resultVO.getUserId(), catalog);
            }

        }
    }

    @Override
    public void mergeCatalog(String userId, Integer catalog) {

        List<RiskUserVarTempModel> tempList = tempRepository.selectVarTempModel(userId, catalog);


        JSONObject jsonObject = new JSONObject();


        for (RiskUserVarTempModel tempModel : tempList) {

            jsonObject.put(tempModel.getVariableKey(), tempModel.getVariableValue());
        }


        saveVarResultModel(userId, catalog, jsonObject);

        tempRepository.deleteVarTempModel(userId, catalog);
    }


    private void saveVarResultModel(String userId, Integer catalog, JSONObject jsonObject) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("catalog").is(catalog));


        RiskUserVarResultModel resultModel = mongoTemplate.findOne(query, RiskUserVarResultModel.class);

        if (resultModel == null) {

            resultModel = new RiskUserVarResultModel();

            resultModel.setUserId(userId);
            resultModel.setCatalog(catalog);
            resultModel.setData(jsonObject);
            resultModel.setCreateTime(new Date());
            resultModel.setUpdateTime(new Date());

            mongoTemplate.insert(resultModel);

        } else {
            Update update = new Update();

            update.set("data", jsonObject);
            update.set("updateTime", new Date());

            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(resultModel.getId())), update, RiskUserVarResultModel.class);
        }
    }

}
