//package org.songbai.loan.service.sms.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.songbai.cloud.basics.exception.BusinessException;
//import org.songbai.loan.common.service.ComAgencyService;
//import org.songbai.loan.constant.user.SmsConstant;
//import org.songbai.loan.model.agency.ExchangeAdminHostModel;
//import org.songbai.loan.model.agency.ExchangeAdminModel;
//import org.songbai.loan.model.sms.EmailNotify;
//import EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import javax.mail.internet.MimeMessage;
//import java.util.Map;
//import java.util.Properties;
//
//
//@Service
//@Slf4j
//public class EmailServiceImpl implements EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//    @Autowired
//    private ComAgencyService comAgencyService;
//
//
//    @Value("${spring.mail.username}")
//    private String sender; //读取配置文件中的参数
//
//    @Override
//    public void sendSimpleMail(String sendTo, String title, String content) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(sender);
//        message.setTo(sendTo);
//        message.setSubject(title);
//        message.setText(content);
//        mailSender.send(message);
//    }
//
////    @Override
////    public void sendTemplateMail(String sendTo, String title, String content, Map<String, Object> param) {
////
////        MimeMessage mimeMessage = mailSender.createMimeMessage();
////
////        try {
////            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
////            helper.setFrom(sender);
////            helper.setTo(sendTo);
////            helper.setSubject(title);
////
////            String text = paramWrapper(content, param);
////            helper.setText(text, true);
////
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////
////        mailSender.send(mimeMessage);
////    }
//
//    @Override
//    public void sendMailByAgency(String sendTo, String title, String content, EmailNotify notify, ExchangeAdminModel model) {
//        try {
//
//            //创建邮件发送服务器
//            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//            mailSender.setHost(model.getEmailHost());
//            mailSender.setPort(model.getEmailPort());
//            mailSender.setUsername(model.getEmailUserName());
//            mailSender.setPassword(model.getEmailPassWord());
//            log.info("host:{}, port:{}, userName:{}", model.getEmailHost(), model.getEmailPort(), model.getEmailUserName());
//            //加认证机制
//            Properties javaMailProperties = new Properties();
//            javaMailProperties.put("mail.smtp.auth", model.getEmailAuth() != null && model.getEmailAuth() != 0);
//            javaMailProperties.put("mail.smtp.starttls.enable", true);
//            javaMailProperties.put("mail.smtp.timeout", 50000);
//            mailSender.setJavaMailProperties(javaMailProperties);
//            //创建邮件内容
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            helper.setFrom(mailSender.getUsername());
//            helper.setTo(sendTo);
//            helper.setSubject(title);
//            helper.setText(paramWrapper(content, notify), true);
//
//            //发送邮件
//            mailSender.send(mimeMessage);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    private String paramWrapper(String content, EmailNotify notify) {
//
//        if (notify == null) {
//            return content;
//        }
//
//        ExchangeAdminModel exchangeAdminModel = comAgencyService.getExchangeInfoByRelationCode(notify.getAgencyCode());
//        if (exchangeAdminModel == null) {
//            return null;
//        }
//        ExchangeAdminHostModel host = comAgencyService.getExchangeHostInfoByRelationCode(notify.getAgencyCode());
//
//        String exchangeName = exchangeAdminModel.getExchangeName();
//        JSONObject jsonObject = JSON.parseObject(exchangeName);
//        content = content.replace("${agencyNameEn}", jsonObject.getString("en"));
//        // url获取方式改变、从ex_exchange_admin_host表获取
//        content = content.replace("${agencyUrl}", host.getDomain());
//        content = content.replace("${agencyNameCn}", jsonObject.getString("zh_CN"));
//        content = content.replace("${code}", notify.getParam().get("code").toString());
//        log.info("email content:{}", content);
//        return content;
//    }
//
//
//}
