//package org.songbai.loan.common.helper;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.math.NumberUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.DateUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.boot.properties.SpringProperties;
//import org.songbai.cloud.basics.exception.BusinessException;
//import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
//import org.songbai.cloud.basics.utils.base.StringUtil;
//import org.songbai.loan.constant.JmsDest;
//import org.songbai.loan.constant.resp.UserRespCode;
//import org.songbai.loan.constant.user.UserConstant;
//import org.songbai.loan.constant.user.UserRedisKey;
//import org.songbai.loan.service.user.util.PhoneUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static org.songbai.loan.constant.user.UserConstant.USER_MSG_COUNT;
//
//@Slf4j
//@Component
//public class SmsSendHelper {
//    private static final String REDIS_SMS_VALID = "user:valid:sms:";
//    private static final String USER_LIMIT_CODE_KEY = "user:msg_code:limit";
//    private static final String USER_IP_LIMIT_CODE_KEY = "user:msg_code:ip:limit";
//
//    private static Logger logger = LoggerFactory.getLogger(SmsSendHelper.class);
//
//    @Autowired
//    private JmsTemplate jmsTemplate;
//    @Autowired
//    private ImgAuthHelper imgAuthHelper;
//
//    @Autowired
//    private SpringProperties properties;
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//
//    public boolean sendMsgCode(String teleCode, String phone, String ip, String imgCode, Integer smsType, String prefix) {
//
//        String redisMsgCodeKey = REDIS_SMS_VALID + StringUtil.trimToEmpty(prefix) + ":" + PhoneUtil.trimFirstZero(teleCode) + phone;
//
//        if (checkImgLimit(phone, ip)) {
//            verifyImgCode(imgCode, prefix);
//        }
//
//        String oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey);
//        if (StringUtils.isNotBlank(oldMsgCode)) {
//            //每次发短信的间隔限制（分）
//            Long msgCodeLimitTime = properties.getLong("user.msgCode_limit_time", UserConstant.USER_MSG_LIMIT_TIME);
//            msgCodeLimitTime *= 60;
//            //缓存剩余失效时间
//            Long redisMsgCodeLeftTime = redisTemplate.getExpire(redisMsgCodeKey, TimeUnit.SECONDS);
//            //每条短信的失效时间（分）
//            Long msgCodeExpireTime = properties.getLong("user.msgCode_expire_time", UserConstant.USER_MSG_EXPIRE_TIME);
//            msgCodeExpireTime *= 60;
//            Long msgCodeLimitTimeLeft = msgCodeLimitTime - (msgCodeExpireTime - redisMsgCodeLeftTime);
//            if (msgCodeLimitTimeLeft > 0) {
//                logger.info("手机号{}的短信验证码限制时间剩余{}秒", phone, msgCodeLimitTimeLeft);
//                throw new BusinessException(UserRespCode.REPEAT_CODE, LocaleKit.get("msg.2041", msgCodeLimitTimeLeft));
//            }
//        }
//
//        String msgCode = String.valueOf(Math.random()).substring(2, 6);
//        Object param = JSON.parse("{\"code\":\"" + msgCode + "\"}");
//
//        redisTemplate.opsForHash().increment(USER_LIMIT_CODE_KEY + phone, phone, 1);
//        redisTemplate.opsForHash().increment(USER_IP_LIMIT_CODE_KEY + ip, ip, 1);
//
//        sendMsgByJms(ip, teleCode, phone, param, smsType);
//        Long msgCodeExpireTime = properties.getLong("user.msgCode_expire_time", UserConstant.USER_MSG_EXPIRE_TIME);
//        //设置失效时间
//        redisTemplate.opsForValue().set(redisMsgCodeKey, msgCode, msgCodeExpireTime, TimeUnit.MINUTES);
//
//
//        Date currentTime = new Date();
//        long end = DateUtils.addDays(DateUtils.truncate(currentTime, Calendar.DAY_OF_MONTH), 1).getTime();
//
//
//        redisTemplate.expire(USER_LIMIT_CODE_KEY + phone, end - currentTime.getTime(), TimeUnit.MILLISECONDS);
//        redisTemplate.expire(USER_IP_LIMIT_CODE_KEY + ip, end - currentTime.getTime(), TimeUnit.MILLISECONDS);
//
//        logger.info("{}的短信验证码{}已存入redis", redisMsgCodeKey, msgCode);
//        return true;
//    }
//
//    /**
//     * 校验验证码是否相同
//     *
//     * @Author：xuesong
//     * @date：15:37 2018/01/26
//     */
//    public boolean checkMsgCode(String teleCode, String phone, String msgCode, String prefix, boolean isDelete) {
//        String redisMsgCodeKey = REDIS_SMS_VALID + StringUtil.trimToEmpty(prefix) + ":" + PhoneUtil.trimFirstZero(teleCode) + phone;
////        String redisMsgCodeKey = REDIS_SMS_VALID + StringUtil.trimToEmpty(prefix) + ":" + phone;
//
//        String smsDev = properties.getString("user.validate.sms.dev");
//
//        if (StringUtils.isNotEmpty(smsDev) && smsDev.equalsIgnoreCase(msgCode)) {
//            return true;
//        }
//
//        String oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey);
//        if (logger.isInfoEnabled())
//            logger.info("oldMsgCode:{},{}", oldMsgCode, redisMsgCodeKey);
//        if (oldMsgCode == null || !oldMsgCode.equals(msgCode)) {
//            return false;
//        } else {
//            if (isDelete) {
//                //用户同类型的短信验证码，可以5分钟内多次使用。
////                redisTemplate.delete(redisMsgCodeKey);
//            }
//            return true;
//        }
//    }
//
//    /**
//     * 检查限制条件
//     *
//     * @Author：xuesong
//     * @date：18:23 2018/01/25
//     */
//    private boolean checkImgLimit(String phone, String ip) {
//        if (redisTemplate.opsForHash().get(USER_LIMIT_CODE_KEY + phone, phone) == null) {
//            return false;
//        }
//        Integer userCount = NumberUtils.toInt((String) redisTemplate.opsForHash().get(USER_LIMIT_CODE_KEY + phone, phone));
//        Integer userImgLimit = properties.getInteger("user:msg:code:day:img:limit", 3);
//        Integer userLimit = properties.getInteger("user:msg:code:day:limit", USER_MSG_COUNT);
//
//        if (userCount != null && userCount >= userLimit) {
//            throw new BusinessException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2042", userLimit));
//        }
//
//        logger.info("ip:{}, key:{}", ip, USER_IP_LIMIT_CODE_KEY + ip);
//        String param = (String) redisTemplate.opsForHash().get(USER_IP_LIMIT_CODE_KEY + ip, ip);
//        Integer ipCount = null;
//        if (StringUtils.isNotBlank(param)) {
//            ipCount = Integer.parseInt(param);
//        }
//
//        Integer ipImgLimit = properties.getInteger("user:msg:code:ip:img:limit", 3);
//        Integer ipLimit = properties.getInteger("user:msg:code:ip:limit", 100);
//
//
//        if (ipCount != null && ipCount >= ipLimit) {
//            throw new BusinessException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2042", ipLimit));
//        }
//
//        boolean needImg = (userCount != null && userCount >= userImgLimit) || (ipCount != null && ipCount >= ipImgLimit);
//
//        return needImg;
////        return false;
////        return needImg || checkRegionMatcher(phone, ip);
//    }
//
////    /**
////     * 校验ip和手机号是否匹配
////     *
////     * @Author：xuesong
////     * @date：18:48 2018/01/25
////     */
////    private boolean checkRegionMatcher(String phone, String ip) {
////        try {
////            HashMap<String, String> param = new HashMap<>();
////            param.put("ip", ip);
////            param.put("phone", phone);
////            String result = HttpTools.doPost("http://lemi.esongbai.com/prevent/matcher/region", param);
////            JSONObject jsonObject = JSONObject.parseObject(result);
////            if (jsonObject.getInteger("code") == 200) {
////                Integer matcher = jsonObject.getJSONObject("data").getInteger("matcher");
////                if (matcher == null || matcher == 0) {
////                    return true;
////                }
////            }
////        } catch (Exception e) {
////            return false;
////        }
////        return false;
////    }
//
//    /**
//     * 发送短信
//     *
//     * @Author：xuesong
//     * @date：18:55 2018/01/25
//     */
//    private void sendMsgByJms(String ip, String teleCode, String phone, Object param, Integer smsType) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("ip", ip);
//        map.put("phone", phone);
//        map.put("teleCode", teleCode);
//        map.put("param", param);
//        map.put("smsType", smsType);
//        map.put("createTime", System.currentTimeMillis());
//        String message = JSON.toJSONString(map);
//        logger.info("发送短信信息：message={}", message);
//        jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
//    }
//
//    private void verifyImgCode(String imgCode, String prefix) {
//
//        if (!imgAuthHelper.isInputImgCode(imgCode)) {
//            throw new BusinessException(UserRespCode.AUTH_IMG_CODE_NEED);
//        } else {
//            boolean is = imgAuthHelper.checkImgCode(imgCode, prefix, true);
//
//            if (!is) {
//                throw new BusinessException(UserRespCode.AUTH_IMG_CODE_FAIL);
//            }
//        }
//    }
//
//
//}
