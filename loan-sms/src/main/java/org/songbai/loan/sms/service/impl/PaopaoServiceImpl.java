package org.songbai.loan.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.finance.HttpTools;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.constant.sms.SmsConstant;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.sms.dao.SmsLogDao;
import org.songbai.loan.sms.model.SmsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("paopaoService")
public class PaopaoServiceImpl extends AbstractSmsSenderServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(PaopaoServiceImpl.class);
    @Autowired
    SpringProperties springProperties;

    @Autowired
    SmsLogDao smsLogDao;

    /**
     * @return
     */
    @Override
    protected SmsLog send0(SmsNotify notify, SmsTemplate senderTemplate, SmsSender senderMessage) {

        if (StringUtil.isEmpty(senderMessage.getData())) {
            logger.info("泡泡云短信>>>没有配置接入号,{}", senderMessage);
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(senderMessage.getData());

        String extno = jsonObject.getString("exton");

        if (StringUtil.isEmpty(extno)) {
            logger.info("泡泡云短信>>>没有解析到接入号,{}", senderMessage);
            return null;
        }

        Map<String, Object> param = notify.getParam();

        String message = messageWrapper(senderTemplate, param);
        String result = "";
        try {
            Map<String, String> data = new HashMap<>();
            data.put("action", "send");
            data.put("account", senderMessage.getAccount());
            data.put("password", senderMessage.getPassword());
            data.put("mobile", notify.getPhone());
            data.put("content", message);
            data.put("extno", extno);
            data.put("rt", "json");
            String url = springProperties.getString("sms.paopaoyun.url", "http://ppyw.paopao106.cn:7862/sms");
            result = HttpTools.doPost(url, data);
            JSONObject response = JSONObject.parseObject(result);
            if (response.getString("status").equals("0")) {
                logger.info("泡泡云发送短信成功,result={}", result);
                try {

                    SmsLog smsLog = new SmsLog();
                    smsLog.setAgencyId(notify.getAgencyId());
                    smsLog.setVestId(notify.getVestId());
                    smsLog.setPhone(notify.getPhone());
                    JSONObject ret = (JSONObject) response.getJSONArray("list").get(0);
                    smsLog.setMid(ret.getString("mid"));
                    smsLog.setCreateTime(new Date());
                    smsLog.setData(response);
                    smsLog.setMsg(message);
                    smsLog.setSenderType(SmsConstant.SenderType.SMS_SENDER_TYPE_PAOPAO.key);
                    return smsLog;

                } catch (Exception e) {
                    return null;
                }

            } else {
                throw new RuntimeException("泡泡云短信发送失败,原因=" + result);
            }


        } catch (Exception e) {
            logger.info("泡泡云发送短信异常，msg={}", result);
            e.printStackTrace();
        }
        return null;
    }


    protected String messageWrapper(SmsTemplate senderTemplate, Map<String, Object> param) {
        String message = "【" + senderTemplate.getSign() + "】" + senderTemplate.getTemplate();
        if (param != null) {
            for (String key : param.keySet()) {
                message = message.replace("{s" + key + "}", "" + param.get(key));
            }
        }
        return message;
    }

    @Override
    protected SmsLogDao getSmsLogDao() {
        return smsLogDao;
    }

}
