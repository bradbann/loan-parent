package org.songbai.loan.common.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.CookieKit;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
public class ImgAuthHelper {
    private static final Logger logger = LoggerFactory.getLogger(ImgAuthHelper.class);
    public static final String COOKIE_SIGN = "img_sign";


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SpringProperties properties;


    public void writeMsgCode(HttpServletResponse response, String sign, String prefix) {

        String redisKey = genRedisKey(sign, prefix);

        ValidateCodeService v = new ValidateCodeService();
        String code = v.getCode();

        redisTemplate.opsForValue().set(redisKey, code, 5 * 60, TimeUnit.SECONDS);

        response.setContentType("image/jpeg");
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0L);
        try {
            ImageIO.write(v.getBuffImg(), "JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteCodeImg(String account, String prefix) {

        String redisKey = genRedisKey(account, prefix);

        redisTemplate.delete(redisKey);
    }


    public boolean checkImgCode(String signCode, String prefix, boolean isDelete) {

        if (StringUtil.isEmpty(signCode)) {
            return false;
        }
        logger.info("图片验证码signCode={}", signCode);
        String[] codeArray = signCode.split(":");
        if (codeArray.length != 3) {
            return false;
        }

        String dev = properties.getString("user.validate.img.dev");

        if (StringUtils.isNotEmpty(dev) && dev.equalsIgnoreCase(codeArray[2])) {
            return true;
        }

        String redisKey = genRedisKey(codeArray[0] + ":" + codeArray[1], prefix);

        String oldMsgCode = redisTemplate.opsForValue().get(redisKey);

        logger.info("旧验证码={},用户输入验证码={}", oldMsgCode, codeArray[2]);

        if (oldMsgCode == null) {
            throw new ResolveMsgException(UserRespCode.AUTH_IMG_CODE_TIMEOUT);
        }

        if (!oldMsgCode.equalsIgnoreCase(codeArray[2])) {

            return false;
        } else {
            if (isDelete) {
                redisTemplate.delete(redisKey);
                {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    if (attributes != null && attributes.getResponse() != null) {
                        CookieKit.deleteCookie(attributes.getResponse(), ImgAuthHelper.COOKIE_SIGN);
                    }
                }
            }
            return true;
        }

    }

    private String genRedisKey(String sign, String prefix) {

        return UserRedisKey.REDIS_MSG_IMG_KEY + sign + ":" + prefix;
    }

    public String mergeSign(HttpServletRequest request, String code) {

        String sign = CookieKit.getCookieAttr(COOKIE_SIGN, request);

        return mergeSign(sign, code);

    }

    public boolean isInputImgCode(String signCode) {


        if (StringUtil.isEmpty(signCode)) {
            return false;
        }
        String[] codeArray = StringUtil.split(signCode, ":");
        if (codeArray.length != 2) {
            return false;
        }

        return StringUtil.isNotEmpty(codeArray[1]);
    }

    public String mergeSign(String sign, String code) {

        return StringUtils.trimToEmpty(sign) + ":" + StringUtils.trimToEmpty(code);
//
//        return sign + ":" + code;
    }
}
