package org.songbai.loan.risk.moxie.magic.jms;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.moxie.magic.service.RiskMoxieMagicService;
import org.songbai.loan.risk.platform.helper.RiskNotifyJmsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static org.songbai.loan.constant.risk.RiskJmsDest.RISK_USER_MOXIE_REPORT;

/**
 * 风控云魔杖准入报告中心
 * Created by mr.czh on 2018/11/8.
 */
@Component
public class MagicRiskUserCenterListener {

    private static final Logger logger = LoggerFactory.getLogger(MagicRiskUserCenterListener.class);

    @Autowired
    RiskMoxieMagicService riskMoxieMagicService;


    @Autowired
    RiskNotifyJmsHelper riskNotifyJmsHelper;


    @JmsListener(destination = RISK_USER_MOXIE_REPORT)
    public void oneMessage(JSONObject jsonObject) {
        logger.info("魔杖准入报告中心接收用户信息:{}", jsonObject);
        if (jsonObject == null) {
            logger.info("用户数据异常>>>>>");
            return;
        }
        String userId = jsonObject.getString("userId");
        String phone = jsonObject.getString("phone");
        String name = jsonObject.getString("name");
        String idCard = jsonObject.getString("idCard");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(name) || StringUtils.isEmpty(idCard)) {
            logger.info("用户数据缺失,不能查询准入报告");
            return;
        }

        try {
            riskMoxieMagicService.getMagicReport2(name, phone, idCard);

            riskNotifyJmsHelper.notifyVariableExtract(VariableConst.VAR_SOURCE_MOXIE_REPORT, userId);
        } catch (Exception e) {
            logger.error("查询魔杖报告异常，{}", e);
        }

    }
}
