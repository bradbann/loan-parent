package org.songbai.loan.risk.mould.variable.extract;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.loan.constant.RestURL;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.model.mould.RiskVariableSourceModel;
import org.songbai.loan.risk.model.user.RiskUserVarTempModel;
import org.songbai.loan.risk.mould.express.Context;
import org.songbai.loan.risk.mould.helper.VariableCalcHelper;
import org.songbai.loan.risk.mould.helper.UserCommonHelper;
import org.songbai.loan.risk.mould.variable.ExtractCalc;
import org.songbai.loan.risk.moxie.magic.model.MagicReportModel;
import org.songbai.loan.risk.moxie.magic.mongo.MagicReportRepository;
import org.songbai.loan.risk.vo.RiskUserVO;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.songbai.loan.risk.vo.VariableMergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service("moxieReportCalc")
public class MoxieReportCalcImpl implements ExtractCalc {

    private static final String SOURCE = VariableConst.VAR_SOURCE_MOXIE_REPORT;
    @Autowired
    MagicReportRepository reportRepository;
    @Autowired
    VariableCalcHelper variableCalcHelper;
    @Autowired
    UserCommonHelper userCommonHelper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SpringProperties properties;

    @Override
    public String source() {
        return SOURCE;
    }

    @Override
    public VariableMergeVO calc(VariableExtractVO calcVO) {

        RiskUserVO riskUserVO = userCommonHelper.userRisk(calcVO.getUserId());
        MagicReportModel reportModel = getMagicReportModel(calcVO, riskUserVO);

        List<RiskVariableSourceModel> sourceModels = variableCalcHelper.getSourceModels(SOURCE);
        JSONObject paramObject = JSONObject.parseObject(reportModel.getData());

        Context context = Context.of(calcVO.getSources())
                .putUser(riskUserVO)
                .putParam(paramObject);

        List<RiskUserVarTempModel> keyMap = variableCalcHelper.calcVariable(sourceModels, context);


        variableCalcHelper.saveVariableTempModel(calcVO.getUserId(), SOURCE, keyMap);

        return VariableMergeVO.builder()
                .catalogs(sourceModels.stream().map(RiskVariableSourceModel::getCatalog).collect(Collectors.toSet()))
                .userId(calcVO.getUserId())
                .sources(SOURCE).build();


    }

    private MagicReportModel getMagicReportModel(VariableExtractVO calcVO, RiskUserVO riskUserVO) {

        if (riskUserVO == null || riskUserVO.getUser() == null || riskUserVO.getUserinfo() == null) {
            log.info("不能找到用户信息，{}", calcVO.getUserId());
            throw new RuntimeException("not found userinfo ,for user:" + calcVO.getUserId() + "order:" + calcVO.getOrderNumber());
        }

//        MagicReportModel reportModel = reportRepository.getMagicReportModelByUserId(calcVO.getUserId());
        // 使用身份证去获取
        MagicReportModel reportModel = reportRepository.getMagicReportModelByIdcard(riskUserVO.getUserinfo().getIdcardNum());

        if (reportTimeout(reportModel)) {
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();

            paramMap.add("userId", calcVO.getUserId());
            paramMap.add("phone", riskUserVO.getUser().getPhone());
            paramMap.add("name", riskUserVO.getUserinfo().getName());
            paramMap.add("idcard", riskUserVO.getUserinfo().getIdcardNum());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());

            JSONObject result = restTemplate.postForObject(RestURL.REQ_MOXIE_REPORT, requestEntity, JSONObject.class);

            if (result.getInteger("code") == RespCode.SUCCESS) {
                reportModel = reportRepository.getMagicReportModelByIdcard(riskUserVO.getUserinfo().getIdcardNum());
//                reportModel = reportRepository.getMagicReportModelByUserId(calcVO.getUserId());
            } else {
                log.error("不能找到 {} 的报表数据,{}", SOURCE, calcVO);
                throw new RuntimeException("不能找到 " + SOURCE + " 的报表数据");
            }
        }
        return reportModel;
    }

    /**
     * 摩羯报告如果不存在或者超时了， 都会触发重新获取摩羯报告。
     *
     * @param reportModel
     * @return
     */
    private boolean reportTimeout(MagicReportModel reportModel) {

        if (reportModel == null ||  reportModel.getUpdateTime() == null) {
            return true;
        }

        long reportTime = reportModel.getUpdateTime().getTime();

        int hour = properties.getInteger("risk.extract.timeout.moxiereport", 72);
        // 魔蝎报告超时时间
        return System.currentTimeMillis() - reportTime > hour * 60 * 1000;
    }


}
