//package org.songbai.loan.service.sms.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
//import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.boot.properties.SpringProperties;
//import org.songbai.loan.model.sms.SmsNotify;
//import org.songbai.loan.model.sms.SmsSender;
//import org.songbai.loan.model.sms.SmsSenderTemplate;
//import SmsLogDao;
//import SmsLog;
//import org.songbai.loan.vo.sms.SenderTemplateVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Service("alidayuSmsService")
//public class AlidayuSmsServiceImpl extends AbstractSmsSenderServiceImpl {
//    private static final Logger logger = LoggerFactory.getLogger(AlidayuSmsServiceImpl.class);
//
//    @Autowired
//    SpringProperties springProperties;
//
//    @Autowired
//    SmsLogDao smsLogDao;
//
//    @Override
//    protected SmsLog send0(SmsNotify notify, SenderTemplateVO senderTemplate,
//                                SmsSender senderMessage) {
//        //短信平台类型
//        String phone = notify.getPhone();
//        String ip = notify.getIp();
//        String extraParam = senderTemplate.getExtraParam();
//        //签名id
//        String templateId = JSON.parseObject(extraParam).getString("templateId");
//        String account = senderMessage.getAccount();
//        String password = senderMessage.getPassword();
//        AlibabaAliqinFcSmsNumSendResponse rsp = null;
//        try {
//            TaobaoClient client = new DefaultTaobaoClient(springProperties.getProperty("sms.alidayu.url"), account,
//                    password);
//            AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//            req.setExtend("111");
//            req.setSmsType("normal");
//            req.setSmsFreeSignName(getSign(senderTemplate));
//            if (notify.getParam() != null) {
//                req.setSmsParamString(JSON.toJSONString(notify.getParam()));
//            }
//            req.setRecNum(phone);
//            req.setSmsTemplateCode(templateId);
//            rsp = (AlibabaAliqinFcSmsNumSendResponse) client.execute(req);
//        } catch (Exception e) {
//            logger.error("阿里大鱼发送短信出错\r\n{}", e);
//        }
//        if (rsp.getErrorCode() != null) {
//            logger.info("阿里大鱼发送短信失败{}", rsp.getBody());
//            SmsLog errorLog = new SmsLog();
//            errorLog.setSender("阿里大鱼");
//            errorLog.setError(rsp.getBody());
//            errorLog.setCreateTime(new Date());
//            errorLog.setMessage(senderTemplate.getTemplate());
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
