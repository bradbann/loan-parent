package org.songbai.loan.risk.mould.helper;

import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.constant.user.UserConstant;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
import org.songbai.loan.risk.vo.RiskResultVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import static org.songbai.loan.constant.risk.RiskConst.Result;


public class RiskResultBuilder {


    private Integer finalScore = 0; // 最终得分 。加权后的得分
    private Integer riskScore = 0; // 风险分得分。

    private boolean stampman = false; //是否标记转人工
    private boolean stampreject = false; //是否标记拒绝
    private Integer[] catalogResult = new Integer[RiskConst.Catalog.values().length];
    private ResultMsgBuilder msgBuilder = new ResultMsgBuilder();


    private RiskMouldModel mouldModel;
    private Integer sex; // 用户性别


    private RiskResultBuilder(RiskMouldModel mouldModel, Integer sex) {
        this.mouldModel = mouldModel;
        this.sex = sex == null ? UserConstant.Sex.DEFAULT.code : sex;
    }

    public static RiskResultBuilder create(RiskMouldModel mouldModel, Integer sex) {
        return new RiskResultBuilder(mouldModel, sex);
    }


    public RiskResultBuilder calc(List<RiskUserMouldCatalogModel> mouldCatalogModels) {
        for (RiskUserMouldCatalogModel umm : mouldCatalogModels) {
            // 有一项为标记拒绝， 那么就直接拒绝
            if (umm.getStampreject() == CommonConst.YES) {
                stampreject = true;
                msgBuilder.stamp(umm, VariableConst.OPER_TYPE_REJECT);
            }
            // 有一项为标记转人工， 那么就直接砖人工
            if (umm.getStampman() == CommonConst.YES) {
                stampman = true;
                msgBuilder.stamp(umm, VariableConst.OPER_TYPE_MAN);
            }
            // 风险分拒绝 与 标记拒绝同等效力
            if (mouldModel.getRiskMaxScore() >= 0
                    && mouldModel.getRiskMaxScore() <= umm.getRiskScore()) {
                stampreject = true;
                msgBuilder.riskScore(umm.getCatalog(), umm.getRiskScore(), mouldModel.getRiskMaxScore());
            }

            catalogResultWrapper(catalogResult, umm);
            finalScore = umm.getFinalScore() + finalScore;
            riskScore = umm.getRiskScore() + riskScore;
        }

        if (sex.equals(UserConstant.Sex.MALE.code)) {
            finalScore = BigDecimal.valueOf(mouldModel.getMaleWeight()).multiply(BigDecimal.valueOf(finalScore)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        } else if (sex.equals(UserConstant.Sex.FEMAIL.code)) {
            finalScore = BigDecimal.valueOf(mouldModel.getFemaleWeight()).multiply(BigDecimal.valueOf(finalScore)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }


        // 风险分拒绝 与 标记拒绝同等效力
        if (mouldModel.getRiskMaxScore() >= 0
                && mouldModel.getRiskMaxScore() <= riskScore) {
            stampreject = true;
            msgBuilder.riskScore(riskScore, mouldModel.getRiskMaxScore());
        }


        return this;
    }


    public RiskResultVO build() {

        Integer realScore = mouldModel.getDefaultSore() + finalScore;

        RiskResultVO.RiskResultVOBuilder builder =
                RiskResultVO.builder()
                        .code(RiskResultVO.CODE_SUCESS)
                        .riskResult(Result.MAN.code)
                        .riskResultList(catalogResult)
                        .riskResultMsg(msgBuilder.getResultMsg())
                        .scoring(realScore);
        // 先判断是 有 直接标记为 拒绝或者转人工的 东西， 如果有 ， 按照标记处理
        if (stampman) {
            return builder.riskResult(Result.MAN.code).build();
        } else if (stampreject) {
            return builder.riskResult(Result.REJECT.code).build();
        } else {

            // 每一项都有一个拒绝县与通过线， 如果用户的拒绝大于30%拒绝，如果大于70% 的通过就直接通过
            if (getPercent(catalogResult, Result.REJECT.code) >= 0.3) {
                return builder.riskResult(Result.REJECT.code).build();
            }

            if (getPercent(catalogResult, Result.PASS.code) >= 0.7) {
                return builder.riskResult(Result.PASS.code).build();
            }

            // 判断总得分是不是 大于通过线， 或者小于拒绝线，如果命中 直接处理掉

            if (mouldModel.getAdoptScore() >= 0 && realScore >= mouldModel.getAdoptScore()) {
                msgBuilder.score(realScore, null, mouldModel.getAdoptScore());
                return builder.riskResultMsg(msgBuilder.getResultMsg()).riskResult(Result.PASS.code).build();
            } else if (mouldModel.getRejectScore() >= 0 && realScore <= mouldModel.getRejectScore()) {
                msgBuilder.score(realScore, mouldModel.getRejectScore(), null);
                return builder.riskResultMsg(msgBuilder.getResultMsg()).riskResult(Result.REJECT.code).build();
            }
        }

        return builder.build();
    }


    private double getPercent(Integer[] catalogResult, Integer code) {

        AtomicInteger counter = new AtomicInteger(0);
        for (Integer i : catalogResult) {

            if (i != null && i.equals(code)) {
                counter.incrementAndGet();
            }
        }

        return new BigDecimal(counter.get()).divide(new BigDecimal(catalogResult.length), 2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    private void catalogResultWrapper(Integer[] catalogResult, RiskUserMouldCatalogModel umm) {
        RiskConst.Catalog catalog = RiskConst.Catalog.parse(umm.getCatalog());

        if (catalog == null) {
            return;
        }

        switch (catalog) {
            case BASIC:
                catalogResult[0] = umm.getFinalResult();
                break;
            case CONTACTS:
                catalogResult[1] = umm.getFinalResult();
                break;
            case CARRIERS:
                catalogResult[2] = umm.getFinalResult();
                break;
            case TAOBAO:
                catalogResult[3] = umm.getFinalResult();
                break;
            case MOXIEREPORT:
                catalogResult[4] = umm.getFinalResult();
                break;
        }
    }


}
