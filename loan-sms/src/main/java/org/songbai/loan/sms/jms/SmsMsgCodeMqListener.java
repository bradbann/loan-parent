package org.songbai.loan.sms.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.constant.sms.SmsConstant;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.sms.dao.SmsDao;
import org.songbai.loan.sms.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.songbai.loan.constant.sms.SmsConstant.*;

@Configuration
@Component
public class SmsMsgCodeMqListener {
    private static Logger logger = LoggerFactory.getLogger(SmsMsgCodeMqListener.class);

    @Autowired
    SmsDao smsDao;

    //@Resource(name = "alidayuSmsService")
    //SmsSenderService smsSenderServiceAli;

    //@Resource(name = "YunxinSmsService")
    //SmsSenderService smsSenderServiceYunxin;
    //
    //@Resource(name = "juheSmsService")
    //SmsSenderService smsSenderServicejuhe;
    //
    @Resource(name = "paopaoService")
    SmsSenderService paopaoService;

    @Resource(name = "chuanglanSmsService")
    SmsSenderService chuanglanSmsService;

    @Autowired
    SpringProperties springProperties;

    private Map<String, SmsTemplate> cached = new ConcurrentHashMap<>();
    private Map<Integer, SmsSender> senderCached = new ConcurrentHashMap<>();

    @JmsListener(destination = JmsDest.SMS_SENT)
    public void onMessage(String message) {


        logger.info("发送短信:接收发送消息{}", message);
        try {


            SmsNotify notify = JSON.parseObject(message, SmsNotify.class);

            SmsSender sender = getSmsSenderMessage(notify.getAgencyId());



            if (sender == null) {
                logger.error(JSONObject.toJSONString(notify), "短信渠道没有找到：{}");
                return;
            }

            SenderType senderType = SenderType.parse(sender.getType());
            if (senderType == null) {
                logger.error(JSONObject.toJSONString(notify), "短信发送类型没有找到：{}", sender);
                return;
            }

            SmsTemplate senderTemplate = getSenderTemplate(notify, sender);

            if (senderTemplate == null) {
                logger.error("agencyId={},vestId={},不能找到发送短信的模板：{} , sender: {}", notify.getAgencyId(),notify.getVestId(),JSONObject.toJSONString(notify), JSONObject.toJSONString(sender));
                return;
            }
            logger.info("使用发送器：{}， 使用的模板内容：{}", sender, senderTemplate.getTemplate());



            switch (senderType) {
                case SMS_SENDER_TYPE_ALI:
                    logger.info("发送短信:调用阿里大鱼平台API");
                    break;
                case SMS_SENDER_TYPE_YUNXIN:
                    logger.info("发送短信:调用云信平台API");
                    break;
                case SMS_SENDER_TYPE_JUHE:
                    logger.info("发送短信:调用聚合平台API");
                    break;
                case SMS_SENDER_TYPE_TLSG:
                    logger.info("发送短信:调用TLSG平台API");
                    break;
                case SMS_SENDER_TYPE_CHUANGLAN:
                    logger.info("发送短信:调用创蓝253平台API");
                    chuanglanSmsService.sendSms(notify, senderTemplate, sender);
                    break;
                case SMS_SENDER_TYPE_PAOPAO:
                    logger.info("发送短信:调用泡泡云平台API");
                    paopaoService.sendSms(notify, senderTemplate, sender);
                    break;
                default:
                    logger.info("发送短信: 没有找到短信渠道类型： " + sender.getType());
            }

        } catch (Exception e) {
            logger.info("发送短信:异常\r\n{}", e);
        }
    }

    void clearTemplateCached() {
        cached.clear();
    }

    void clearSenderCached() {
        senderCached.clear();
    }

    private SmsTemplate getSenderTemplate(SmsNotify notify, SmsSender sender) {
        String key = notify.getAgencyId() + "_" + notify.getSmsType() + "_" + notify.getVestId() + "_" + sender.getId();

        logger.info("短信模板缓存key={}", key);
        SmsTemplate templateVO = cached.get(key);

        if (templateVO == null) {
            logger.info("短信模板缓存没有,去数据库中查询,notify={},sender={}", notify, sender);
            templateVO = internalSenderTemplate(notify, sender);

            if (templateVO != null) {
                cached.putIfAbsent(key, templateVO);
            }
        }
        return templateVO;
    }


    private SmsTemplate internalSenderTemplate(SmsNotify notify, SmsSender sender) {


        return  smsDao.getSmsTemplate(notify.getAgencyId(), notify.getSmsType(), notify.getVestId(), sender.getId());

        //if (template == null) {
        //    template = smsDao.getDefaultSenderTemplate(notify.getAgencyId(), notify.getSmsType(), sender.getId());
        //    if (template == null) {
        //        logger.info("发送短信:没有配置默认短信模板,agencyId={}", notify.getAgencyId());
        //    }
        //}
        //return template;
    }

    private SmsSender getSmsSenderMessage(Integer agencyId) {
        SmsSender sender = senderCached.get(agencyId);
        if (sender != null) {
            return sender;
        }
        SmsSender senderMessage = smsDao.getSenderMessage(CommonConst.STATUS_VALID, agencyId);

        if (senderMessage == null) {
            logger.info("发送短信:没有找到激活的短信服务商,agencyId={}", agencyId);
            return null;
        }
        senderCached.put(agencyId, senderMessage);
        return senderMessage;
    }
}
