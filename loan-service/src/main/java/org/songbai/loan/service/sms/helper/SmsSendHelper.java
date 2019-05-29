package org.songbai.loan.service.sms.helper;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.helper.ImgAuthHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.model.sms.SmsVoiceModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.sms.dao.ComSmsVoiceSenderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class SmsSendHelper {
    private static final String REDIS_SMS_VALID = "user:valid:sms:";
    private static final String USER_LIMIT_CODE_KEY = "user:msg_code:limit:";
    private static final String USER_IP_LIMIT_CODE_KEY = "user:msg_code:ip:limit:";

    private static Logger logger = LoggerFactory.getLogger(SmsSendHelper.class);

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ImgAuthHelper imgAuthHelper;

    @Autowired
    private SpringProperties properties;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ComSmsVoiceSenderDao voiceSenderDao;

    public boolean sendMsgCode(Integer agencyId, AppVestModel vestModel, String phone, String ip, Integer sendType, String lhbk_uuid,String landCode,SmsConst.Type smsType) {


        checkImgLimit(phone, ip, agencyId, vestModel);

        String redisMsgCodeKey = REDIS_SMS_VALID + vestModel.getVestCode() + ":" + StringUtil.trimToEmpty(smsType.code) + ":" + phone;

        String oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey);
        if (StringUtils.isNotBlank(oldMsgCode)) {
            //每次发短信的间隔限制（分）
            Long msgCodeLimitTime = properties.getLong("user.msgCode_limit_time", 5L);
            msgCodeLimitTime *= 60;
            //缓存剩余失效时间
            Long redisMsgCodeLeftTime = redisTemplate.getExpire(redisMsgCodeKey, TimeUnit.SECONDS);
            //每条短信的失效时间（分）
            Long msgCodeExpireTime = properties.getLong("user.msgCode_expire_time", 5L);
            msgCodeExpireTime *= 60;
            Long msgCodeLimitTimeLeft = msgCodeLimitTime - (msgCodeExpireTime - redisMsgCodeLeftTime);
            if (msgCodeLimitTimeLeft > 0) {
                logger.info("手机号{}的短信验证码限制时间剩余{}秒", phone, msgCodeLimitTimeLeft);
                throw new BusinessException(UserRespCode.REPEAT_CODE, LocaleKit.get("msg.2041", msgCodeLimitTimeLeft));
            }
        }
        String msgCode = String.valueOf(Math.random()).substring(2, 6);
        if (sendType == CommonConst.YES) {
            SmsVoiceModel voiceModel = voiceSenderDao.findAgencySenderVoice(agencyId);
            if (voiceModel == null) {
                throw new BusinessException(UserRespCode.USER_MSG_SEND);
            }
            sendVoiceMsgByJms(phone, msgCode, agencyId);
        } else {
            Object param = JSON.parse("{\"code\":\"" + msgCode + "\"}");
            sendMsgByJms(ip, phone, param, smsType.value, agencyId, vestModel.getId());
        }
        redisTemplate.opsForHash().increment(USER_LIMIT_CODE_KEY + vestModel.getVestCode() + phone, phone, 1);
        redisTemplate.opsForHash().increment(USER_IP_LIMIT_CODE_KEY + agencyId + ip, ip, 1);


        Long msgCodeExpireTime = properties.getLong("user.msgCode_expire_time", 5L);
        //设置失效时间
        redisTemplate.opsForValue().set(redisMsgCodeKey, msgCode, msgCodeExpireTime, TimeUnit.MINUTES);


        Date currentTime = new Date();
        long end = DateUtils.addDays(DateUtils.truncate(currentTime, Calendar.DAY_OF_MONTH), 1).getTime();


        redisTemplate.expire(USER_LIMIT_CODE_KEY + vestModel.getVestCode() + phone, end - currentTime.getTime(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(USER_IP_LIMIT_CODE_KEY + agencyId + ip, end - currentTime.getTime(), TimeUnit.MILLISECONDS);

        logger.info("{}的短信验证码{}已存入redis", redisMsgCodeKey, msgCode);
        logger.info("短信发送成功>>>>>,vestCode={},phone={},landCode={},lhbk_uuid={},sendeType={},ip={}", vestModel.getVestCode(), phone, landCode, lhbk_uuid, sendType == 0 ? "短信" : "语音", ip);
        return true;
    }

    /**
     * 校验验证码是否相同
     *
     * @Author：xuesong
     * @date：15:37 2018/01/26
     */
    public boolean checkMsgCode(AppVestModel vestModel, String phone, String msgCode, String prefix) {
        String redisMsgCodeKey = REDIS_SMS_VALID + vestModel.getVestCode() + ":" + StringUtil.trimToEmpty(prefix) + ":" + phone;
        String smsDev = properties.getString("user.validate.sms.dev");
        if (StringUtils.isNotEmpty(smsDev) && smsDev.equalsIgnoreCase(msgCode)) {
            return true;
        }
        String oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey);
        if (logger.isInfoEnabled()) {
            logger.info("oldMsgCode:{},{}", oldMsgCode, redisMsgCodeKey);
        }

        return oldMsgCode != null && oldMsgCode.equals(msgCode);
    }

    /**
     * 检查限制条件
     *
     * @Author：xuesong
     * @date：18:23 2018/01/25
     */
    private boolean checkImgLimit(String phone, String ip, Integer agencyId, AppVestModel vestModel) {
        if (redisTemplate.opsForHash().get(USER_LIMIT_CODE_KEY + vestModel.getVestCode() + phone, phone) == null) {
            return false;
        }
        Integer userCount = NumberUtils.toInt((String) redisTemplate.opsForHash().get(USER_LIMIT_CODE_KEY + vestModel.getVestCode() + phone, phone));
        Integer userImgLimit = properties.getInteger("user:msg:code:day:img:limit", 3);
        Integer userLimit = properties.getInteger("user:msg:code:day:limit", 5);//用户每日发送短信次数的限制

        if (userCount >= userLimit) {
            throw new BusinessException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2042", userLimit));
        }

        logger.info("ip:{}, key:{}", ip, USER_IP_LIMIT_CODE_KEY + agencyId + ip);
        String param = (String) redisTemplate.opsForHash().get(USER_IP_LIMIT_CODE_KEY + agencyId + ip, ip);
        Integer ipCount = null;
        if (StringUtils.isNotBlank(param)) {
            ipCount = Integer.parseInt(param);
        }

        Integer ipImgLimit = properties.getInteger("user:msg:code:ip:img:limit", 3);
        Integer ipLimit = properties.getInteger("user:msg:code:ip:limit", 100);


        if (ipCount != null && ipCount >= ipLimit) {
            throw new BusinessException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2042", ipLimit));
        }

        return userCount >= userImgLimit || ipCount != null && ipCount >= ipImgLimit;
    }

    /**
     * 发送语音短信
     *
     */
    private void sendVoiceMsgByJms(String tele,  String code, Integer agencyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", tele);
        map.put("agencyId", agencyId);
        map.put("voiceCode", code);
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        logger.info("发送短信信息：message={}", message);
        jmsTemplate.convertAndSend(JmsDest.SMS_VOICE_SENT, message);
    }
    /**
     * 发送短信
     *
     * @Author：xuesong
     * @date：18:55 2018/01/25
     */
    private void sendMsgByJms(String ip, String tele, Object param, Integer smsType, Integer agencyId, Integer vestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("ip", ip);
        map.put("phone", tele);
        map.put("param", param);
        map.put("smsType", smsType);
        map.put("agencyId", agencyId);
        map.put("vestId", vestId);
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        logger.info("发送短信信息：message={}", message);
        jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
    }

    private void verifyImgCode(String imgCode, String prefix) {

        if (!imgAuthHelper.isInputImgCode(imgCode)) {
            throw new BusinessException(UserRespCode.AUTH_IMG_CODE_NEED);
        } else {
            boolean is = imgAuthHelper.checkImgCode(imgCode, prefix, true);

            if (!is) {
                throw new BusinessException(UserRespCode.AUTH_IMG_CODE_FAIL);
            }
        }
    }
}
