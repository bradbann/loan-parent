package org.songbai.loan.risk.service;

import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;

import java.util.List;

public interface RiskMouldService {
    RiskMouldModel selectRiskMouldModel();

    List<Integer> selectMouldVariableCatalog(Integer mouldId);

    List<RiskMouldWeightModel> selectMouldWeightListByMouldId(Integer id);

    List<RiskMouldVariableModel> selectMouldVarList(Integer catalog, Integer modelId);

    RiskMouldModel selectRiskMouldModelByThirdId(String userid);

    RiskMouldModel selectMouldById(Integer mouldId);
}
