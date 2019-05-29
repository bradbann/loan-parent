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
import org.songbai.loan.risk.service.statis.helper.UserContactHelper;
import org.songbai.loan.risk.vo.RiskUserVO;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.songbai.loan.risk.vo.VariableMergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("platformContactCalc")
@Slf4j
public class PlatformContactCalcImpl implements ExtractCalc {

    private static final String SOURCE = VariableConst.VAR_SOURCE_PLATFORM_CONTACTS;
    @Autowired
    VariableCalcHelper variableCalcHelper;
    @Autowired
    UserCommonHelper userCommonHelper;
    @Autowired
    UserContactHelper userContactHelper;


    @Override
    public String source() {
        return SOURCE;
    }

    @Override
    public VariableMergeVO calc(VariableExtractVO calcVO) {
        RiskUserVO riskUserVO = userCommonHelper.userRisk(calcVO.getUserId());

        if (riskUserVO == null || riskUserVO.getUser() == null) {
            throw new RuntimeException("不能找到 用户[" + calcVO.getUserId() + "] 的信息");
        }

        JSONObject paramObject = userContactHelper.getRiskParam(riskUserVO.getUser().getId());

        if (paramObject == null) {
            log.error("不能找到 {} 的报表数据,{}", SOURCE, calcVO);
            throw new RuntimeException("不能找到 " + SOURCE + " 的报表数据");
        }

        List<RiskVariableSourceModel> sourceModels = variableCalcHelper.getSourceModels(SOURCE);


        Context context = Context.of(calcVO.getSources())
                .putUser(riskUserVO)
                .putParam(paramObject);

        List<RiskUserVarTempModel> tempModels = variableCalcHelper.calcVariable(sourceModels, context);


        variableCalcHelper.saveVariableTempModel(calcVO.getUserId(), SOURCE, tempModels);

        return VariableMergeVO.builder()
                .catalogs(sourceModels.stream().map(RiskVariableSourceModel::getCatalog).collect(Collectors.toSet()))
                .userId(calcVO.getUserId())
                .sources(SOURCE).build();


    }


}
