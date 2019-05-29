package org.songbai.loan.risk.moxie.magic.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.moxie.magic.model.MagicReportModel;
import org.songbai.loan.risk.moxie.magic.mongo.MagicReportRepository;
import org.songbai.loan.risk.moxie.magic.service.RiskMoxieMagicService;
import org.songbai.loan.risk.moxie.magic.util.MoxieMagicMethod;
import org.songbai.loan.risk.moxie.magic.util.MoxieSignUtils;
import org.songbai.loan.risk.platform.service.UserDataTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.songbai.loan.risk.moxie.magic.util.MoxieMagicMethod.ReqCommonParams;
import static org.songbai.loan.risk.moxie.magic.util.MoxieMagicMethod.ReqCommonParamsValue;

/**
 * Created by mr.czh on 2018/11/8.
 */
@Service
@Slf4j
public class RiskMoxieMagicServiceImpl implements RiskMoxieMagicService {

    private static final Logger logger = LoggerFactory.getLogger(RiskMoxieMagicService.class);

    @Autowired
    MagicReportRepository magicReportRepository;

    @Autowired
    UserDataTaskService userDataTaskService;

    @Autowired
    SpringProperties properties;

    @Value("${moxie.magicreport.url}")
    private String apiUrl;
    @Value("${moxie.magicreport.appid}")
    private String appId;
    @Value("${moxie.magicreport.privateKey}")
    private String privateKey;


    @Override
    public void getMagicReport2(String userName, String mobile, String idcard) {


        MagicReportModel model = magicReportRepository.getMagicReportModelByIdcard(idcard);


        int timeout = properties.getInteger("risk.extract.timeout.moxiereport", 72);

        if (notTimeout(model, timeout)) {
            log.info("moxie report info[{}] not timeout and used :{}", idcard, model.getId());
            return;
        }

        log.info("moxie report info timeout and require :{}", idcard);

        String report = requireMagicReport(userName, mobile, idcard);


        if (report == null) {
            logger.info("请求魔杖准入报告异常:userName:{},mobile:{},idcard:{}", userName, mobile, idcard);
            return;
        }
        model = saveMagicReportModel(model, userName, mobile, idcard, report);


        userDataTaskService.saveDataTask(genUserId(idcard),
                VariableConst.VAR_SOURCE_MOXIE_REPORT,
                model.getId(), RiskConst.Task.DATA_SUCCESS, "success");


    }

    @Override
    @Transactional
    public void getMagicReport(String userName, String mobile, String idcard, String userId) {
        String report = requireMagicReport(userName, mobile, idcard);

        if (report == null) {
            logger.info("请求魔杖准入报告异常:userName:{},mobile:{},idcard:{},userid:{}", userName, mobile, idcard, userId);
            return;
        }
        saveMagicReportModel(userName, mobile, idcard, userId, report);
    }


    /**
     * 获取报告
     *
     * @param userName
     * @param mobile
     * @param idcard
     */
    private String requireMagicReport(String userName, String mobile, String idcard) {
        //魔杖准入报告
        String method = MoxieMagicMethod.Method.MagicWand2.getMethod();
        /* 请求参数 */
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put(ReqCommonParams.METHOD, method);
        reqParams.put(ReqCommonParams.APP_ID, appId);
        reqParams.put(ReqCommonParams.VERSION, ReqCommonParamsValue.VERSION);
        reqParams.put(ReqCommonParams.FORMAT, ReqCommonParamsValue.FORMAT);
        reqParams.put(ReqCommonParams.SIGN_TYPE, ReqCommonParamsValue.SIGN_TYPE);
        reqParams.put(ReqCommonParams.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        /* 业务参数 */
        Map<String, String> bizParams = new HashMap<>();
        bizParams.put("name", userName);
        bizParams.put("mobile", mobile);
        bizParams.put("idcard", idcard);
        String bizContent = null;
        try {
            logger.info("请求魔杖申请准入报告：{}", bizParams);
            bizContent = new ObjectMapper().writeValueAsString(bizParams);
            reqParams.put(ReqCommonParams.BIZ_CONTENT, bizContent);
            //签名
            String sign = MoxieSignUtils.signSHA1WithRSA(reqParams, privateKey);
            reqParams.put(ReqCommonParams.SIGN, sign);
            String resContent = HttpTools.doGet(apiUrl, reqParams);
            logger.info("魔杖申请准入报告请求返回:\n {}", resContent);
            if (StringUtils.isNotBlank(resContent)) {
                JSONObject object = JSON.parseObject(resContent);
                if (object.getBoolean("success") && object.getString("code").equals("0000")) {
                    return object.getString("data");
                } else {
                    throw new BusinessException(600, object.getString("msg"));
                }
            }
        } catch (JsonProcessingException e) {
            logger.info("请求魔杖准入报告异常:\n {}", e);
            throw new BusinessException(600, "请求魔杖准入报告异常");
        }

        return null;
    }


    private void saveMagicReportModel(String userName, String mobile, String idcard, String userId, String data) {

        MagicReportModel model = new MagicReportModel();

        model.setIdcard(idcard);
        model.setUserId(userId);
        model.setName(userName);
        model.setPhone(mobile);
        model.setData(data);
        model.setUpdateTime(new Date());


        MagicReportModel oldModel = magicReportRepository.getMagicReportModelByUserId(userId);

        if (oldModel != null) {
            model.setId(oldModel.getId());
        } else {
            model.setCreateTime(new Date());
        }

        magicReportRepository.save(model);

        userDataTaskService.saveDataTask(userId,
                VariableConst.VAR_SOURCE_MOXIE_REPORT,
                model.getId(), RiskConst.Task.DATA_SUCCESS, "success");

    }


    private MagicReportModel saveMagicReportModel(MagicReportModel oldModel, String userName, String mobile, String idcard, String data) {

        MagicReportModel model = new MagicReportModel();

        model.setIdcard(idcard);
        model.setUserId(genUserId(idcard));
        model.setName(userName);
        model.setPhone(mobile);
        model.setData(data);
        model.setUpdateTime(new Date());

        if (oldModel != null) {
            model.setId(oldModel.getId());
        } else {
            model.setCreateTime(new Date());
        }

        magicReportRepository.save(model);

        return model;
    }


    private boolean notTimeout(MagicReportModel model, int timeout) {

        return model != null && model.getUpdateTime() != null &&
                (System.currentTimeMillis() - model.getUpdateTime().getTime() < timeout * 60 * 1000);
    }

    private String genUserId(String idcard) {

        return "idcard:" + idcard;
    }


}
