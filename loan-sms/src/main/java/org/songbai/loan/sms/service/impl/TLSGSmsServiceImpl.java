//package org.songbai.loan.sms.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.telesign.MessagingClient;
//import com.telesign.RestClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.boot.properties.SpringProperties;
//import org.songbai.loan.model.sms.SmsNotify;
//import org.songbai.loan.model.sms.SmsSender;
//import org.songbai.loan.sms.dao.SmsLogDao;
//import org.songbai.loan.sms.model.SmsLog;
//import org.songbai.loan.vo.sms.SenderTemplateVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Date;
//import java.util.Map;
//
//@Service("TLSGSmsService")
//public class TLSGSmsServiceImpl extends AbstractSmsSenderServiceImpl {
//
//    private static final Logger logger = LoggerFactory.getLogger(JuheSmsServiceImpl.class);
//    @Autowired
//    SpringProperties springProperties;
//
//    @Autowired
//    SmsLogDao smsLogDao;
//
//    @Override
//    protected SmsLog send0(SmsNotify notify, SenderTemplateVO senderTemplate,
//                                SmsSender senderMessage) {
//        ////String mobile = notify.getTeleCode() + notify.getPhone();
//        //String extraParam = senderTemplate.getExtraParam();
//        //String messageType = JSON.parseObject(extraParam).getString("templateId");
//        //String customerId = senderMessage.getAccount();
//        //String apiKey = senderMessage.getPassword();
//        //Map<String, Object> param = notify.getParam();
//        //MessagingClient messagingClient = new MessagingClient(customerId, apiKey);
//        //
//        //String message = messageWrapper(senderTemplate, param);
//        //
//        //
//        //try {
//        //    RestClient.TelesignResponse telesignResponse = messagingClient.message(mobile, message, messageType, null);
//        //    if (telesignResponse.ok) {
//        //        logger.info("result", "TLSG发送短信成功");
//        //        return null;
//        //    } else {
//        //        logger.info("TLSG发送短信失败，msg={}", telesignResponse.body);
//        //        SmsLog errorLog = new SmsLog();
//        //        errorLog.setSender("TLSG");
//        //        errorLog.setError(telesignResponse.body);
//        //        errorLog.setCreateTime(new Date());
//        //        //errorLog.setChannel(senderMessage.getChannelId());
//        //        errorLog.setMessage(senderTemplate.getTemplate());
//        //        errorLog.setAccount(customerId);
//        //        return errorLog;
//        //    }
//        //} catch (IOException e) {
//        //    logger.info("TLSG发送短信异常，msg={}", e);
//        //} catch (GeneralSecurityException e) {
//        //    logger.info("TLSG发送短信异常，msg={}", e);
//        //}
//        return null;
//    }
//
//
//    @Override
//    protected SmsLogDao getSmsLogDao() {
//        return smsLogDao;
//    }
//
//}
