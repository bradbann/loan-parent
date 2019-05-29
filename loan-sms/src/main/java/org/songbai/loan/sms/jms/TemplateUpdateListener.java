package org.songbai.loan.sms.jms;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.TemplateConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TemplateUpdateListener {

    public static Logger logger = LoggerFactory.getLogger(TemplateUpdateListener.class);
    //    @Autowired
//    EmailSmgCodeListener emailSmgCodeListener;
    @Autowired
    SmsMsgCodeMqListener smsMsgCodeMqListener;

    @JmsListener(destination = JmsDest.SMS_TEMPLATE_UPDATE)
    public void onMessage(String message) {
        logger.info("短信发送渠道信息发生改变信息{}", message);


        JSONObject json = JSONObject.parseObject(message);

        int type = json.getInteger("type");

        if (type == TemplateConst.Type.SMS_TEMPLATE.type) {
            smsMsgCodeMqListener.clearTemplateCached();
        } else if (type == TemplateConst.Type.SMS_SENDER.type) {
            smsMsgCodeMqListener.clearSenderCached();
        } else if (type == TemplateConst.Type.SMS_TEMPLATE_SENDER.type) {
            smsMsgCodeMqListener.clearTemplateCached();
        }
        //else if (type == TemplateConst.Type.EMAIL_TEMPLATE.type) {
//            emailSmgCodeListener.clearCached();
//        }
    }


}
