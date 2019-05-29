//package org.songbai.loan.sms.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.boot.properties.SpringProperties;
//import org.songbai.cloud.basics.utils.http.HttpTools;
//import org.songbai.loan.model.sms.SmsNotify;
//import org.songbai.loan.model.sms.SmsSender;
//import org.songbai.loan.sms.dao.SmsLogDao;
//import org.songbai.loan.sms.model.SmsLog;
//import org.songbai.loan.vo.sms.SenderTemplateVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service("YunxinSmsService")
//public class YunxinSmsServiceImpl extends AbstractSmsSenderServiceImpl {
//    private static final Logger logger = LoggerFactory.getLogger(YunxinSmsServiceImpl.class);
//
//    @Autowired
//    SpringProperties springProperties;
//
//    @Autowired
//    SmsLogDao smsLogDao;
//
//
//    @Override
//    protected SmsLog send0(SmsNotify notify, SenderTemplateVO senderTemplate,
//                                SmsSender senderMessage) {
//        String account = senderMessage.getAccount();
//        String password = senderMessage.getPassword();
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("userCode", account);
//        params.put("userPass", password);
//        params.put("DesNo", notify.getPhone());
//        logger.info(JSON.toJSONString(notify.getParam()));
//
//        String content = messageWrapper(senderTemplate, notify.getParam());
//
//        String msg = content + "【" + getSign(senderTemplate) + "】";
//        params.put("Msg", msg);
//        params.put("Channel", springProperties.getProperty("sms.yunxin.Channel"));
//        String rsp = HttpTools.urlPost(springProperties.getProperty("sms.yunxin.url"), params, "utf-8", true);
//        Integer start = rsp.indexOf("<string xmlns=\"http://tempuri.org/\">");
//        Integer end = rsp.indexOf("</string>");
//        String result = rsp.substring(start + "<string xmlns=\"http://tempuri.org/\">".length(), end);
//        if ('-' == result.charAt(0)) {
//            logger.info("云信发送短信失败{}", result);
//            SmsLog errorLog = new SmsLog();
//            errorLog.setSender("云信");
//            errorLog.setError(result);
//            errorLog.setCreateTime(new Date());
//            errorLog.setMessage(msg);
//            errorLog.setAccount(account);
//            errorLog.setPassword(password);
//            return errorLog;
//        }
//        return null;
//    }
//
//    @Override
//    protected SmsLogDao getSmsLogDao() {
//        return smsLogDao;
//    }
//
//}
