package org.songbai.loan.sms.jms;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsVoiceModel;
import org.songbai.loan.sms.dao.SmsVoiceSenderDao;
import org.songbai.loan.sms.service.SmsVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Author: qmw
 * Date: 2019/2/15 2:27 PM
 */
@Component
public class SmsVoiceListener {
    private static final Logger logger = LoggerFactory.getLogger(SmsVoiceListener.class);
    @Autowired
    private SmsVoiceService smsVoiceService;
    @Autowired
    private SmsVoiceSenderDao voiceSenderDao;

    @JmsListener(destination = JmsDest.SMS_VOICE_SENT)
    public void onMessage(String message) {


        logger.info("发送语音短信:接收发送消息{}", message);
        try {

            SmsNotify notify = JSON.parseObject(message, SmsNotify.class);
            SmsVoiceModel voiceModel = voiceSenderDao.findAgencySenderVoice(notify.getAgencyId());
            if (voiceModel == null) {
                logger.info("发送语音短信未启用或未配置:agency={}", notify.getAgencyId());
                return;
            }
            smsVoiceService.sendVoiceMsg(voiceModel, notify);

        } catch (Exception e) {
            logger.info("发送短信:异常\r\n{}", e);
        }
    }
}
