//package org.songbai.loan.service.sms.jms;
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.songbai.cloud.basics.utils.base.StringUtil;
//import org.songbai.loan.common.dao.ComExchangeDao;
//import org.songbai.loan.common.service.ComAgencyService;
//import org.songbai.loan.common.service.CountryLangService;
//import org.songbai.loan.constant.JmsDest;
//import org.songbai.loan.constant.agency.AgencyConstans;
//import org.songbai.loan.constant.sms.SmsConst;
//import org.songbai.loan.model.admin.CountryLangModel;
//import org.songbai.loan.model.agency.ExchangeAdminModel;
//import org.songbai.loan.model.sms.EmailNotify;
//import org.songbai.loan.model.sms.EmailTemplateModel;
//import EmailTemplateDao;
//import EmailService;
//import org.songbai.loan.service.user.util.PhoneUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Slf4j
//public class EmailSmgCodeListener {
//    @Autowired
//    private EmailTemplateDao templateDao;
//
//    @Autowired
//    private EmailService emailService;
//    @Autowired
//    private ComAgencyService comAgencyService;
//    @Autowired
//    private CountryLangService countryLangService;
//    @Autowired
//    private ComExchangeDao comExchangeDao;
//
//    Map<String, EmailTemplateModel> cached = new ConcurrentHashMap<>();
//
//
//    @JmsListener(destination = JmsDest.EMAIL_SENT)
//    public void onMessage(String message) {
//        log.info("发送邮件:接收发送消息{}", message);
//        try {
//            EmailNotify notify = JSON.parseObject(message, EmailNotify.class);
//
//            notify.setTeleCode(PhoneUtil.trimFirstZero(notify.getTeleCode()));
//
//            EmailTemplateModel templateModel = getEmailTemplateModel(notify);
//
//            if (templateModel == null) {
//                log.error("不能找到邮件模板：{}", JSONObject.toJSONString(notify));
//                return;
//            }
//
//            if (StringUtils.isBlank(notify.getAgencyCode())) {
//                log.error(">>>>sendEmail fail,agencyCode is null !");
//                return;
//            }
//
//            ExchangeAdminModel model = comAgencyService.getExchangeInfoByRelationCode(notify.getAgencyCode());
//            if (model == null || StringUtil.isEmpty(model.getEmailHost()) || StringUtil.isEmpty(model.getEmailUserName())
//                    || StringUtil.isEmpty(model.getEmailPassWord()) || StringUtil.isEmpty(model.getEmailAuth())
//                    || StringUtil.isEmpty(model.getEmailAddress())) {
//                log.error(">>>>sendEmail fail,param is error!model={}", JSON.toJSONString(model));
//                return;
//            }
//
//            String emailContent = notify.getEmail();
//
//            CountryLangModel langModel = countryLangService.getCountryInfoByTeleCode(notify.getTeleCode());
//            if (langModel != null && StringUtils.isNotBlank(langModel.getSymbol()) && StringUtils.isNotBlank(model.getCompanyName())) {
//                JSONObject exchangeJson = JSON.parseObject(model.getCompanyName());
//                if (exchangeJson != null && exchangeJson.get(langModel.getSymbol()) != null) {
//                    emailContent = emailContent.replaceAll("\\$\\{companyName}", exchangeJson.getString(langModel.getSymbol()));
//                }
//            }
//
////            emailService.sendTemplateMail(notify.getEmail(), templateModel.getName(), templateModel.getTemplate(), notify.getParam());
//            emailService.sendMailByAgency(emailContent, templateModel.getName(), templateModel.getTemplate(), notify, model);
//        } catch (Exception e) {
//            log.info("发送短信:异常\r\n{}", e);
//        }
//    }
//
//
//    void clearCached() {
//        cached.clear();
//    }
//
//    private EmailTemplateModel getEmailTemplateModel(EmailNotify notify) {
//
//        EmailTemplateModel templateModel = internalEmailTemplateModel(notify);
//        return templateModel;
//    }
//
//
//    private EmailTemplateModel internalEmailTemplateModel(EmailNotify notify) {
//        if ("0".equals(notify.getTeleCode())) {
//            notify.setTeleCode("00");
//        }
//        EmailTemplateModel templateModel = templateDao.getEmailTemplateModel(notify.getEmailType(), notify.getTeleCode(), notify.getAgencyCode());
//
//        if (templateModel == null) {
//            templateModel = templateDao.getEmailTemplateModel(notify.getEmailType(), notify.getTeleCode(), notify.getAgencyCode());
//            if (templateModel != null) {
//                ExchangeAdminModel param = new ExchangeAdminModel();
//                param.setAgencyRelationCode(notify.getAgencyCode());
//                ExchangeAdminModel model = comExchangeDao.selectOne(param);
//                if (model == null) {
//                    return templateModel;
//                }
//                String name = templateModel.getName();
//                String exchangeName = model.getExchangeName();
//                JSONObject json = JSON.parseObject(exchangeName);
//                templateModel.setName(name.replace("${agencyNameEn}", (String) json.get("en")));
//                return templateModel;
//            }
//            if (AgencyConstans.Agency_Platfrom.equals(notify.getAgencyCode())) {
//                return null;
//            }
//            log.info("teleCode:{}, emailType:{}", notify.getTeleCode(), notify.getEmailType());
//            templateModel = templateDao.getEmailTemplateModel(notify.getEmailType(), notify.getTeleCode(), AgencyConstans.Agency_Platfrom);
//            if (templateModel == null) {
//                return null;
//            }
//        }
//        ExchangeAdminModel param = new ExchangeAdminModel();
//        param.setAgencyRelationCode(notify.getAgencyCode());
//        ExchangeAdminModel model = comExchangeDao.selectOne(param);
//        if (model == null) {
//            return templateModel;
//        }
//        String name = templateModel.getName();
//        String exchangeName = model.getExchangeName();
//        JSONObject json = JSON.parseObject(exchangeName);
//        templateModel.setName(name.replace("${agencyNameEn}", (String) json.get("en")));
//
//        return templateModel;
//    }
//
//    private String genKey(EmailNotify notify) {
//
//        return notify.getEmailType() + "_" + notify.getTeleCode() + "_" + notify.getAgencyCode();
//    }
//
//
//}
