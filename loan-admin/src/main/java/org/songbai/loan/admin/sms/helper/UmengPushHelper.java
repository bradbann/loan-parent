//package org.songbai.loan.admin.sms.helper;
//
//import java.util.Locale;
//
//import org.apache.commons.lang3.LocaleUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
//import org.songbai.loan.constant.JmsDest;
//import org.songbai.loan.constant.sms.UmengPushTypeCons;
//import org.songbai.loan.model.msg.UmengSoecktMsgModel;
//import org.songbai.loan.model.user.UserModel;
//import org.songbai.loan.service.ComUserService;
//import org.songbai.loan.vo.sms.UmengSmsPushVO;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSONObject;
//
//@Component
//public class UmengPushHelper {
//    private static final Logger logger = LoggerFactory.getLogger(UmengPushHelper.class);
//    @Autowired
//    ComUserService commonUserService;
//    @Autowired
//    JmsTemplate jmsTemplate;
//
//    public void sendUmengPush(Integer userId, Integer type, JSONObject json) {
//        if (logger.isInfoEnabled())
//            logger.info(">>>admin友盟消息推送，userId={},type={},json={}", userId, type, json);
//        if (userId == null) {
//            logger.info("userId为空！");
//            return;
//        }
//
//        if (type == null) {
//            logger.info("type为空！");
//            return;
//        }
//
//        UmengPushTypeCons.Type smsType = UmengPushTypeCons.Type.parse(type);
//        if (smsType == null) {
//            if (logger.isInfoEnabled())
//                logger.info("smsType转译为空，type={}", type);
//            return;
//        }
//        UmengSmsPushVO vo = new UmengSmsPushVO();
//        JSONObject valueJson = new JSONObject();
//        String title = null;
//        String text = null;
//
//        UserModel user = commonUserService.selectUserModelById(userId);
//        Locale locale = LocaleUtils.toLocale(user.getLang());
//
//
//        switch (smsType) {
//            case AUTH_SUCC:
//                title = LocaleKit.get("msg.1500", locale);
//                text = LocaleKit.get("msg.1501", locale);
//                break;
//            case AUTH_FAIL:
//                title = LocaleKit.get("msg.1502", locale);
//                text = LocaleKit.get("msg.1503", locale);
//                break;
//            case DRAW_AUTH_SUCC:
//                title = LocaleKit.get("msg.1508", locale);
//                text = LocaleKit.get("msg.1509", locale, json.getDoubleValue("drawValue"), json.getString("coinType"));
//                break;
//            case DRAW_AUTH_FAIL:
//                title = LocaleKit.get("msg.1510", locale);
//                text = LocaleKit.get("msg.1511", locale, json.getDoubleValue("drawValue"), json.getString("coinType"), json.getString("remark"));
//                break;
//            default:
//                break;
//        }
//
//        vo.setUserId(userId);
//        vo.setTitle(title);
//        vo.setText(text);
//        vo.setContentType(type);
//        vo.setSendAction(smsType.action);
//        String uuid = System.currentTimeMillis() + "_" + userId + "_" + type;
//        vo.setUuid(uuid);
//
//        jmsTemplate.convertAndSend(JmsDest.UMENG_SMS_PUSH_SINGLE, vo);
//
//        //socket推送
//        UmengSoecktMsgModel socket = new UmengSoecktMsgModel();
//        BeanUtils.copyProperties(vo, socket);
//        jmsTemplate.convertAndSend(JmsDest.QUEUE_UMENG_MSG, socket);
//    }
//}
