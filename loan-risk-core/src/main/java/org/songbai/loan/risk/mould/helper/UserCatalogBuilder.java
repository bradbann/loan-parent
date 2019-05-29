package org.songbai.loan.risk.mould.helper;

import com.alibaba.fastjson.JSONObject;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserCatalogBuilder {

    private final RiskMouldModel mouldModel;

    private int riskScore = 0;//  风险级别得分
    private int scoring = 0; // 计分项得分


    private boolean stampman = false; //是否标记转人工
    private boolean stampreject = false; //是否标记拒绝


    private Integer finalScore = null;
    private Integer finalResult = null;
    private List<RiskMouldVariableModel> hitVariable;
    private JSONObject variableParam;

    private UserCatalogBuilder(RiskMouldModel mouldModel) {
        this.mouldModel = mouldModel;
        this.hitVariable = new ArrayList<>();
    }

    public static UserCatalogBuilder create(RiskMouldModel mouldModel) {
        return new UserCatalogBuilder(mouldModel);
    }

    public UserCatalogBuilder variableParam(JSONObject variableParam) {
        this.variableParam = variableParam;
        return this;
    }


    public UserCatalogBuilder riskLevel(Integer riskLevel) {
        if (riskLevel == VariableConst.RISK_LEVEL_LOWER && mouldModel.getLowerRiskScore() >= 0) {
            riskScore += mouldModel.getLowerRiskScore();
        } else if (riskLevel == VariableConst.RISK_LEVEL_MIDDLE && mouldModel.getMiddleRiskScore() >= 0) {
            riskScore += mouldModel.getMiddleRiskScore();
        } else if (riskLevel == VariableConst.RISK_LEVEL_HIGH && mouldModel.getHighRiskScore() >= 0) {
            riskScore += mouldModel.getHighRiskScore();
        }

        return this;
    }

    public UserCatalogBuilder oper(Integer operType, Integer operScore) {

        if (operType == VariableConst.OPER_TYPE_SCORE) {

            scoring += operScore;
        } else if (operType == VariableConst.OPER_TYPE_REJECT) {

            stampreject = true;
        } else if (operType == VariableConst.OPER_TYPE_MAN) {
            stampman = true;
        }

        return this;
    }


    public UserCatalogBuilder weight(RiskMouldWeightModel weightModel) {

        if (weightModel.getWeight() > 0) {
            finalScore = BigDecimal.valueOf(scoring).multiply(BigDecimal.valueOf(weightModel.getWeight())).intValue();
        } else {
            finalScore = scoring;
        }


        if (stampman) {
            finalResult = RiskConst.Result.MAN.code;
        } else if (stampreject) {
            finalResult = RiskConst.Result.REJECT.code;
        } else if (mouldModel.getRiskMaxScore() >= 0 && mouldModel.getRiskMaxScore() <= riskScore) {
            finalResult = RiskConst.Result.REJECT.code;
        } else if (weightModel.getAdoptScore() >= 0 && weightModel.getAdoptScore() <= finalScore) {
            finalResult = RiskConst.Result.PASS.code;
        } else if (weightModel.getRejectScore() >= 0 && weightModel.getRejectScore() >= finalScore) {
            finalResult = RiskConst.Result.REJECT.code;
        } else {
            finalResult = RiskConst.Result.DEFAULT.code;
        }


        return this;
    }

    public UserCatalogBuilder hitVariable(RiskMouldVariableModel variableModel, String param) {
        RiskMouldVariableModel model = new RiskMouldVariableModel();
        BeanUtils.copyProperties(variableModel, model);

        model.setCreateTime(null);
        model.setUpdateTime(null);
        model.setId(null);
        model.setStatus(null);
        model.setIndexed(null);

        model.setRemark(param);

        hitVariable.add(model);
        return this;
    }


    public RiskUserMouldCatalogModel build() {

        return build(new RiskUserMouldCatalogModel());
    }

    public RiskUserMouldCatalogModel build(RiskUserMouldCatalogModel userMouldModel) {

        userMouldModel.setScoring(scoring);
        userMouldModel.setRiskScore(riskScore);
        userMouldModel.setStampman(stampman ? CommonConst.YES : CommonConst.NO);
        userMouldModel.setStampreject(stampreject ? CommonConst.YES : CommonConst.NO);
        userMouldModel.setMouldId(mouldModel.getId());

        userMouldModel.setFinalResult(finalResult);
        userMouldModel.setFinalScore(finalScore);
        userMouldModel.setHitVariable(hitVariable);
        userMouldModel.setVariableParam(variableParam);

        return userMouldModel;
    }

}



