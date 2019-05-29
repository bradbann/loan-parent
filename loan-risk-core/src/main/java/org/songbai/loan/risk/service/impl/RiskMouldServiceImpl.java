package org.songbai.loan.risk.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.dao.RiskMouldDao;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.service.RiskMouldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class RiskMouldServiceImpl implements RiskMouldService {

    @Autowired
    private RiskMouldDao riskMouldDao;


    @Override
    public RiskMouldModel selectRiskMouldModel() {
        RiskMouldModel mouldModel = riskMouldDao.selectRiskMouldModel();

        if (mouldModel == null) {
            log.error("不能查询到启用的风控模型");
            throw new RuntimeException("不能查询到启用的风控模型");
        }
        return mouldModel;
    }


    @Override
    public List<Integer> selectMouldVariableCatalog(Integer mouldId) {
        return riskMouldDao.selectMouldVariableCatalog(mouldId);
    }

    @Override
    public List<RiskMouldWeightModel> selectMouldWeightListByMouldId(Integer mouldId) {
        return riskMouldDao.selectMouldWeightListByMouldId(mouldId);
    }

    @Override
    public List<RiskMouldVariableModel> selectMouldVarList(Integer catalog, Integer modelId) {
        return riskMouldDao.selectMouldVarList(catalog, modelId);
    }


    @Override
    public RiskMouldModel selectRiskMouldModelByThirdId(String thirdId) {
        return riskMouldDao.selectRiskMouldModelByThirdId(thirdId);
    }

    @Override
    public RiskMouldModel selectMouldById(Integer mouldId) {
        return riskMouldDao.selectMouldById(mouldId);
    }
}
