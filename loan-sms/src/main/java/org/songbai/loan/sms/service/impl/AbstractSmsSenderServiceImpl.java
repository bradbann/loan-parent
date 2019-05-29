package org.songbai.loan.sms.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.sms.dao.SmsLogDao;
import org.songbai.loan.sms.model.SmsLog;
import org.songbai.loan.sms.mongo.SmsLogRepository;
import org.songbai.loan.sms.service.SmsSenderService;
import org.songbai.loan.vo.sms.SenderTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractSmsSenderServiceImpl implements SmsSenderService {
    private Logger logger = LoggerFactory.getLogger(AbstractSmsSenderServiceImpl.class);


    @Autowired
    private SpringProperties springProperties;
    @Autowired
    private SmsLogRepository smsLogRepository;

    @Override
    public void sendSms(SmsNotify notify, SmsTemplate senderTemplate,
                        SmsSender senderMessage) {

        SmsLog smsLog = send0(notify, senderTemplate, senderMessage);
        //
        if (smsLog != null) {
            logger.info("发送短信:短信发送成功日志{}存入mongoDB", smsLog);
            smsLogRepository.insert(smsLog);
        }
    }

    protected abstract SmsLog send0(SmsNotify notify, SmsTemplate senderTemplate,
                                    SmsSender senderMessage);

    protected abstract SmsLogDao getSmsLogDao();


    protected String messageWrapper(SenderTemplateVO senderTemplate, Map<String, Object> param) {
        String message = senderTemplate.getTemplate();
        if (param != null) {
            for (Iterator iterator = param.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                message = message.replace("${" + key + "}", "" + param.get(key));
            }
        }
        return message;
    }

    protected String getSign(SenderTemplateVO senderTemplate) {

        String sign = springProperties.getProperty("sms.alidayu.sign");


        if (StringUtil.isEmpty(senderTemplate.getSign())) {
            return sign;
        }

        return senderTemplate.getSign();
    }

}
