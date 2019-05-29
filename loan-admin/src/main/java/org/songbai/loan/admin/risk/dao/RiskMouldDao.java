package org.songbai.loan.admin.risk.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.risk.model.RiskMouldVo;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.mould.RiskVariableModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mr.czh on 2018/11/6.
 */
public interface RiskMouldDao extends BaseMapper<RiskMouldModel> {

    List<RiskMouldVo> findList(@Param("page") Integer page, @Param("pageSize") Integer pageSize, @Param("model") RiskMouldVo vo);

    Integer queryCount(@Param("model") RiskMouldVo vo);

    void addRiskMould(RiskMouldVo vo);

    void updateRiskMould(RiskMouldVo vo);

    void updateRiskRule(RiskMouldModel vo);

    void addRiskModelWeight(RiskMouldWeightModel params);

    void updateRiskWeight(RiskMouldWeightModel model);

    Integer queryCountWeight(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog);

    List<RiskMouldWeightModel> selectRiskMouldWeightByMouldId(@Param("mouldId") Integer mouldId);

    List<RiskMouldVariableModel> selectMouldVariableByMouldId(@Param("mouldId") Integer mouldId);

    void addVariable(RiskMouldVariableModel model);

    void updateVariable(RiskMouldVariableModel model);

    void delVariable(@Param("id") Integer id);

    Integer variableCount(@Param("catalog") Integer cataLog, @Param("variableCode") String variableCode);

    List<RiskVariableModel> variableDrop();

    List<RiskVariableModel> riskTagDrop(@Param("catalog") Integer catalog);

    RiskMouldModel getRiskById(@Param("id") Integer id);

    void deleteVariableModelByCatalogAndCode(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog, @Param("variableCode") String variableCode);


    List<RiskMouldVariableModel> selectVariableModelByCatalogAndCode(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog, @Param("variableCode") String variableCode);

    Integer selectVariableModelMaxIndexdByCatalogAndCode(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog);


    HashMap<String, Object> selectVariableModelMaxScore(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog);


    List<HashMap<String, Object>> selectVariableListForMaxMinScore(@Param("mouldId") Integer mouldId, @Param("catalog") Integer catalog);
}
