package org.songbai.loan.service.sms.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.CookieKit;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.common.helper.ImgAuthHelper;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.dao.ComAgencyDao;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.sms.helper.SmsSendHelper;
import org.songbai.loan.service.sms.service.ComSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Author: qmw
 * Date: 2018/10/29 下午5:22
 */
@Service
public class ComSmsServiceImpl implements ComSmsService {
    @Autowired
    private SmsSendHelper smsSendHelper;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    private ComAgencyDao comAgencyDao;
    @Autowired
    ImgAuthHelper imgAuthHelper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ComSmsServiceImpl.class);

    @Override
    public void sendMsgCode(String phone, Integer type, String imgCode, HttpServletRequest request, String landCode, Integer sendType) {
        Assert.notNull(type, LocaleKit.get("common.param.notnull", "type"));
        Assert.hasText(phone, LocaleKit.get("common.param.notnull", "phone"));
        String ip = IpUtil.getIp(request);

        String vestCode = PlatformKit.parseChannel(request);

        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }
        if (StringUtil.isNotEmpty(imgCode)) {
            String host = HeaderKit.getHost(request);
            if (!host.contains(":")) {
                host = host + ":";
            }
            if (!imgAuthHelper.checkImgCode(host + landCode + phone + ":" + imgCode, SmsConst.Type.COMMON.code, false)) {
                throw new BusinessException(UserRespCode.AUTH_IMG_CODE_FAIL);
            }
        }
        SmsConst.Type smsType = SmsConst.Type.parse(type);
        if (smsType == null) {
            throw new BusinessException(UserRespCode.PARAM_VALIDATE_TYPE, "验证码类型异常");
        }
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        Integer vestId = null;

        if (StringUtil.isNotEmpty(landCode)) {
            AgencyChannelModel channel = comAgencyService.findChannelByLandCode(landCode, agencyId);
            if (channel != null) {
                vestId = channel.getVestId();
            }
        }
        AppVestModel vestModel;
        if (vestId == null && StringUtil.isEmpty(vestCode)) {
            vestModel = comAgencyDao.findDefualtVest(agencyId);
            if (vestModel == null) {
                logger.info("马甲id,马甲code未传,agencyId={}不存在默认马甲", agencyId);
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }
        } else {
            vestModel = comAgencyService.findVestByIdOrVestCode(agencyId, vestId, vestCode);
        }

        if (vestModel == null) {
            logger.info("马甲不存在,vestId={},vestCode={},agencyId={}", vestId, vestCode, agencyId);
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }
        if (StringUtil.isNotEmpty(landCode)) {
            String redisKey = UserRedisKey.REDIS_MSG_LIMIT_KEY + agencyId + "_" + phone;

            if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
                redisTemplate.opsForValue().increment(redisKey, 1);
                if (StringUtils.isEmpty(imgCode)) {
                    throw new BusinessException(UserRespCode.IMG_CODE_NEED);
                }
            } else {
                LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
                redisTemplate.expire(redisKey, seconds, TimeUnit.SECONDS);
            }
        }
        String lhbk_uuid = CookieKit.getCookieAttr("lhbk_uuid", request);
        logger.info(">>>>发送短信,vestCode={},phone={},imgCode={},landCode={},lhbk_uuid={},sendeType={},ip={}", vestCode, phone, imgCode, landCode, lhbk_uuid, sendType == 0 ? "短信" : "语音", ip);
        switch (smsType) {
            case COMMON:
                smsSendHelper.sendMsgCode(agencyId, vestModel, phone, ip, sendType, lhbk_uuid,landCode, SmsConst.Type.COMMON);
                break;
            //case REGISTER:
            //    UserModel userModel = comUserService.findUserModelByAgencyIdAndPhone(phone, agencyModel.getId());
            //    if (userModel != null) {
            //        throw new BusinessException(UserRespCode.ACCOUNT_ALREADY_REGISTER); // 用户已经存在， 请登录
            //    }
            //    smsSendHelper.sendMsgCode(agencyModel.getId(), channelId, phone, ip, SmsConst.Type.REGISTER);
            //    break;
            //case RESET:
            //    Integer userId = UserUtil.getUserId();
            //    if (userId == null) {
            //        throw new BusinessException(UserRespCode.AUTH_NOT_AUTH); // 用户已经存在， 请登录
            //    }
            //    userModel = comUserService.findUserModelByAgencyIdAndPhone(phone, agencyModel.getId());
            //    if (userModel == null) {
            //        break;
            //    }
            //    smsSendHelper.sendMsgCode(agencyModel.getId(),channelId, phone, ip, SmsConst.Type.RESET);
            //    break;
            default:
                break;
        }
    }

    @Override
    public boolean checkMsgCodeForPhone(AppVestModel vestModel, String phone, String msgCode, SmsConst.Type type) {
        Assert.notNull(type, LocaleKit.get("common.param.notnull", "type"));
        Assert.hasText(phone, LocaleKit.get("common.param.notnull", "phone"));
        return smsSendHelper.checkMsgCode(vestModel, phone, msgCode, type.code);
    }
}
