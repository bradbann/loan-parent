package org.songbai.loan.risk.mould.variable.extract;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.model.mould.RiskVariableSourceModel;
import org.songbai.loan.risk.model.user.RiskUserVarTempModel;
import org.songbai.loan.risk.mould.express.Context;
import org.songbai.loan.risk.mould.helper.VariableCalcHelper;
import org.songbai.loan.risk.mould.helper.UserCommonHelper;
import org.songbai.loan.risk.mould.variable.ExtractCalc;
import org.songbai.loan.risk.moxie.taobao.model.TaobaoReportModel;
import org.songbai.loan.risk.moxie.taobao.mongo.TaobaoReportRepository;
import org.songbai.loan.risk.vo.VariableMergeVO;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service("moxieTaobaoReportCalc")
public class MoxieTaobaoReportCalcImpl implements ExtractCalc {

    private static final String SOURCE = VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT;


    @Autowired
    TaobaoReportRepository reportRepository;
    @Autowired
    VariableCalcHelper variableCalcHelper;
    @Autowired
    UserCommonHelper userCommonHelper;


    @Override
    public String source() {
        return SOURCE;
    }

    @Override
    public VariableMergeVO calc(VariableExtractVO calcVO) {
        TaobaoReportModel reportDataModel = reportRepository.getReportData(calcVO.getUserId(), calcVO.getTaskId());

        if (reportDataModel == null) {
            log.error("不能找到 {} 的报表数据,{}", SOURCE, calcVO);
            throw new RuntimeException("不能找到 " + SOURCE + " 的报表数据");
        }

        List<RiskVariableSourceModel> sourceModels = variableCalcHelper.getSourceModels(SOURCE);
        JSONObject paramObject = JSONObject.parseObject(reportDataModel.getReportData());


        Context context = Context.of(calcVO.getSources())
                .putUser(userCommonHelper.userRisk(calcVO.getUserId()))
                .putParam(paramObject);

        List<RiskUserVarTempModel> keyMap = variableCalcHelper.calcVariable(sourceModels, context);


        variableCalcHelper.saveVariableTempModel(calcVO.getUserId(), SOURCE, keyMap);

        return VariableMergeVO.builder()
                .catalogs(sourceModels.stream().map(RiskVariableSourceModel::getCatalog).collect(Collectors.toSet()))
                .userId(calcVO.getUserId())
                .sources(SOURCE).build();
    }


}
