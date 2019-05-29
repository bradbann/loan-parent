package org.songbai.loan.risk.mould.variable.over;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.dao.UserRiskOrderDao;
import org.songbai.loan.risk.model.user.RiskUserVarResultModel;
import org.songbai.loan.risk.mould.variable.ExtractCalcFactory;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DataPrepareHelper {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExtractCalcFactory extractCalcFactory;

    @Autowired
    private SpringProperties properties;

    @Autowired
    UserRiskOrderDao riskOrderDao;


    public void prepareForBase(RiskOrderVO vo, Integer catalog) {


        RiskUserVarResultModel model = getRiskUserVarResultModel(vo.getThridId(), catalog);

        int timeout = properties.getInteger("risk.extract.timeout.platform", 24);

        if (notTimeout(model, timeout)) {
            log.info("platform base info[{}] not timeout and used var_result:{}", vo, model.getId());
            return;
        }

        log.info("platform base info timeout and require :{}", vo);

        VariableExtractVO vo2 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_PLATFORM_BASE)
                .orderNumber(vo.getOrderNumber()).userId(vo.getThridId()).build();

        extractCalcFactory.extractAndMerge(vo2);
    }


    public void prepareForContact(RiskOrderVO vo, Integer catalog) {
        RiskUserVarResultModel model = getRiskUserVarResultModel(vo.getThridId(), catalog);

        int timeout = properties.getInteger("risk.extract.timeout.platform", 24);

        if (notTimeout(model, timeout)) {
            log.info("platform contact info[{}] not timeout and used var_result:{}", vo, model.getId());
            return;
        }

        log.info("platform contact info timeout and require :{}", vo);

        VariableExtractVO vo1 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_PLATFORM_CONTACTS)
                .orderNumber(vo.getOrderNumber()).userId(vo.getThridId()).build();

        extractCalcFactory.extractAndMerge(vo1);
    }


    public void prepareForMoxieReport(RiskOrderVO vo, Integer catalog) {
        RiskUserVarResultModel model = getRiskUserVarResultModel(vo.getThridId(), catalog);

        int timeout = properties.getInteger("risk.extract.timeout.moxiereport", 72);

        if (notTimeout(model, timeout)) {
            log.info("moxie report info[{}] not timeout and used var_result:{}", vo, model.getId());
            return;
        }

        log.info("moxie report info timeout and require :{}", vo);

        VariableExtractVO vo3 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_MOXIE_REPORT)
                .orderNumber(vo.getOrderNumber()).userId(vo.getThridId()).build();

        extractCalcFactory.extractAndMerge(vo3);
    }


    public void prepareForCarrierReport(RiskOrderVO vo, Integer catalog) {
        RiskUserVarResultModel model = getRiskUserVarResultModel(vo.getThridId(), catalog);

        int timeout = properties.getInteger("risk.extract.timeout.carrier", 72);

        if (notTimeout(model, timeout)) {
            log.info("carrier report info[{}] not timeout and used var_result:{}", vo, model.getId());
            return;
        }

        log.info("carrier report info timeout and require :{}", vo);

        String taskId = riskOrderDao.getTaskIdByUserIdAndSource(vo.getThridId(), VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT);

        if (StringUtils.isEmpty(taskId)) {
            log.info("carrier report info[{}] not found taskId and jump", vo);
            return;
        }
        VariableExtractVO vo3 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT)
                .taskId(taskId)
                .orderNumber(vo.getOrderNumber()).userId(vo.getThridId()).build();

        extractCalcFactory.extractAndMerge(vo3);
    }

    public void prepareForTaobaoReport(RiskOrderVO vo, Integer catalog) {
        RiskUserVarResultModel model = getRiskUserVarResultModel(vo.getThridId(), catalog);

        int timeout = properties.getInteger("risk.extract.timeout.taobao", 72);

        if (notTimeout(model, timeout)) {
            log.info("taobao report info[{}] not timeout and used var_result:{}", vo, model.getId());
            return;
        }

        log.info("taobao report info timeout and require :{}", vo);

        String taskId = riskOrderDao.getTaskIdByUserIdAndSource(vo.getThridId(), VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT);

        if (StringUtils.isEmpty(taskId)) {
            log.info("carrier report info[{}] not found taskId and jump", vo);
            return;
        }

        VariableExtractVO vo3 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT).taskId(taskId)
                .orderNumber(vo.getOrderNumber()).userId(vo.getThridId()).build();

        extractCalcFactory.extractAndMerge(vo3);
    }


    private boolean notTimeout(RiskUserVarResultModel model, int timeout) {

        return model != null && model.getUpdateTime() != null &&
                (System.currentTimeMillis() - model.getUpdateTime().getTime() < timeout * 60 * 1000);
    }

    private RiskUserVarResultModel getRiskUserVarResultModel(String userId, Integer catalog) {
        Criteria criteria = Criteria.where("userId").is(userId).and("catalog").is(catalog);

        return mongoTemplate.findOne(Query.query(criteria), RiskUserVarResultModel.class);
    }


}
