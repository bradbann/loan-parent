package org.songbai.loan.risk.mould.helper;

import org.apache.commons.lang3.StringUtils;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;

import java.util.ArrayList;
import java.util.List;

public class ResultMsgBuilder {


    private List<String> resultMsg = new ArrayList<>();


    public void stamp(RiskUserMouldCatalogModel model) {

        stamp(model, null);
    }


    public void stamp(RiskUserMouldCatalogModel model, Integer operType) {

        List<RiskMouldVariableModel> list = model.getHitVariable();

        if (list == null || list.isEmpty()) {
            return;
        }


        for (RiskMouldVariableModel var : list) {

            RiskConst.CalcSymbol symbol = null;
            if (StringUtils.isEmpty(var.getCalcSymbol())
                    || (symbol = RiskConst.CalcSymbol.parse(var.getCalcSymbol())) == null
                    || var.getOperType() == null
                    || var.getOperType() == VariableConst.OPER_TYPE_SCORE) {
                continue;
            }

            if (operType != null && !var.getOperType().equals(operType)) {
                continue;
            }

            if (StringUtils.isEmpty(var.getCalcLeft())) {
                resultMsg.add(var.getVariableName() + "(" + var.getRemark() + ") " + symbol.name + " " + var.getCalcRight());
            } else {
                resultMsg.add(var.getVariableName() + "(" + var.getRemark() + "), 匹配" + var.getCalcLeft() + " " + symbol.code + " " + var.getCalcRight());
            }
        }


    }


    public void score(Integer score, Integer minScore, Integer maxScore) {

        StringBuilder sb = new StringBuilder();

        sb.append("总得分").append(score).append("分");

        if (minScore != null && score < minScore) {
            sb.append(",不足").append(minScore).append("分");
        }
        if (maxScore != null && maxScore > score) {
            sb.append(",超过").append(maxScore).append("分");
        }
        resultMsg.add(sb.toString());
    }


    public void score(RiskConst.Catalog catalog, Integer score, Integer minScore, Integer maxScore) {
        StringBuilder sb = new StringBuilder();

        sb.append(catalog.name + "得分").append(score).append("分");

        if (minScore != null && score < minScore) {
            sb.append(",不足").append(minScore).append("分");
        }
        if (maxScore != null && maxScore > score) {
            sb.append(",超过").append(maxScore).append("分");
        }
        resultMsg.add(sb.toString());
    }

    public void riskScore(Integer score, Integer maxScore) {

        resultMsg.add("风控得分达到" + score + "分, 超过" + maxScore + "分");
    }

    public void riskScore(Integer catalog, Integer score, Integer maxScore) {

        RiskConst.Catalog catalog1 = RiskConst.Catalog.parse(catalog);

        if (catalog1 != null) {
            resultMsg.add(catalog1.name + "风控得分达到" + score + "分, 超过" + maxScore + "分");
        }
    }


    public List<String> getResultMsg() {
        return resultMsg;
    }

    public String resultMsg() {
        return StringUtils.join(resultMsg, ",");
    }
}
