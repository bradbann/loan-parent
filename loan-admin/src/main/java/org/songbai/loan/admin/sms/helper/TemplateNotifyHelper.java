package org.songbai.loan.admin.sms.helper;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static org.songbai.loan.constant.sms.TemplateConst.*;

@Component
public class TemplateNotifyHelper {


    private static final Logger logger = LoggerFactory.getLogger(TemplateNotifyHelper.class);

    @Autowired
    private JmsTemplate jmsTemplate;


    public void notifySmsSender(Integer dataId) {


        notifySms(Type.SMS_SENDER, dataId);
    }


    public void notifySmsTemplate(Integer dataId) {


        notifySms(Type.SMS_TEMPLATE, dataId);
    }

    public void notifySmsTemplateSender(Integer dataId) {


        notifySms(Type.SMS_TEMPLATE_SENDER, dataId);
    }


    public void notifyEmailTemplate(Integer dataId) {


        notifySms(Type.EMAIL_TEMPLATE, dataId);
    }


    public void notifySms(Type type, Integer dataId) {
        String msg = paramWrapper(type.type, type.msg, dataId);

        logger.info("notify sms update template info : {}",msg );

        jmsTemplate.convertAndSend(JmsDest.SMS_TEMPLATE_UPDATE, msg);
    }


    public String paramWrapper(int code, String msg, Integer dataId) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", code);
        jsonObject.put("msg", msg);
        jsonObject.put("dataId", dataId);


        return jsonObject.toJSONString();
    }

}
