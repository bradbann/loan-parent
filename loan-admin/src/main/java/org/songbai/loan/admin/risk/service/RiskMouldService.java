package org.songbai.loan.admin.risk.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.risk.model.RiskMouldVariableVo;
import org.songbai.loan.admin.risk.model.RiskMouldVo;
import org.songbai.loan.admin.risk.model.RiskVariableDropVo;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.mould.RiskVariableModel;

import java.util.List;

/**
 * 风控模型
 * Created by mr.czh on 2018/11/6.
 */
public interface RiskMouldService {

    Page<RiskMouldVo> findByPage(Integer page, Integer pageSize,RiskMouldVo vo);

    RiskMouldModel getRiskById(Integer id);

    void addRiskMould(RiskMouldVo vo);

    void copyRiskMould(String name, Integer status, Integer mouldId);

    void updateRiskMould(RiskMouldVo vo);

    void updateRiskRule(RiskMouldModel vo);

    void updateRiskWeight(RiskMouldWeightModel model);

    Response addRiskWeight(RiskMouldWeightModel model);

    List<RiskMouldWeightModel> riskRuleList(RiskMouldWeightModel model);

    List<RiskMouldVariableVo> riskVariableList(RiskMouldVariableModel model);

    void saveMouldVariable(Integer mouldId, List<RiskMouldVariableModel> list);

    void delVariable(Integer id);

    List<RiskVariableDropVo> variableDrop();

    List<RiskVariableModel> riskTagDrop(Integer catalog);
}
