package org.songbai.loan.user.user.controller;

import com.baomidou.mybatisplus.toolkit.IdWorker;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.mvc.user.TokenUtil;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.CookieKit;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.common.helper.ImgAuthHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.sms.SmsVoiceModel;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.user.UserUvModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.sms.dao.ComSmsVoiceSenderDao;
import org.songbai.loan.service.sms.service.ComSmsService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.model.po.UserLoginPO;
import org.songbai.loan.user.user.model.po.UserRegisterPO;
import org.songbai.loan.user.user.mongo.UserUvDao;
import org.songbai.loan.user.user.service.UserOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.songbai.loan.constant.rediskey.UserRedisKey.REDIS_KEY;
import static org.songbai.loan.constant.resp.UserRespCode.ACCOUNT_NOT_EXISTS;
import static org.songbai.loan.constant.resp.UserRespCode.VLIDATE_ERROR_REPEAT;

@RestController
@RequestMapping("/user")
public class UserOptCtrl {

    @Autowired
    private ComSmsService comSmsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ComUserService comUserService;
    //@Autowired
    //private UserOptLogService logService;
    @Autowired
    private UserOptService userOptService;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    ImgAuthHelper imgAuthHelper;
    @Autowired
    private ComSmsVoiceSenderDao comSmsVoiceSenderDao;
    @Autowired
    UserUvDao userUvDao;
    private Logger logger = LoggerFactory.getLogger(UserOptCtrl.class);
    @Autowired
    SpringProperties properties;

    /**
     * 描述:发送短信验证码
     */
    @PostMapping("/senMsgCode")
    @ResponseBody
    @LimitLess
    public Response sendMsgCode(String phone, Integer type, String imgCode, String landCode, @RequestParam(defaultValue = "0") Integer sendType, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String lhbk_uuid = CookieKit.getCookieAttr("lhbk_uuid", request);

        logger.info("调用发送短信>>>>,phone={},imgCode={},landCode={},lhbk_uuid={},sendeType={},ip={}", phone, imgCode, landCode, lhbk_uuid, sendType == 0 ? "短信" : "语音", ip);

        Assert.notNull(type, LocaleKit.get("common.param.notnull", "type"));
        SmsConst.Type smsType = SmsConst.Type.parse(type);
        if (smsType == null) {
            throw new BusinessException(UserRespCode.PARAM_VALIDATE_TYPE, LocaleKit.get("msg.2011"));
        }
        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }
        if (StringUtil.isNotEmpty(landCode)) {
            Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

            AgencyChannelModel channel = comAgencyService.findOneChannelByLandCode(landCode, agencyId);
            if (channel == null) {
                logger.info("landCode={},没有对应的渠道", landCode);
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }
            AppVestModel vestModel = comAgencyService.findOneVestByIdOrVestCode(agencyId, channel.getVestId(), null);
            if (vestModel == null) {
                logger.info("马甲不存在,vestId={},,agencyId={}", channel.getVestId(), agencyId);
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }
            UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(phone, channel.getVestId(), agencyId);
            if (dbModel != null) {
                logger.info("落地页发送短信>>>>用户已存在,vestCode={},phone={},imgCode={},landCode={},lhbk_uuid={},sendeType={},ip={}", vestModel.getVestCode(), phone, imgCode, landCode, lhbk_uuid, sendType == 0 ? "短信" : "语音", ip);
                throw new BusinessException(UserRespCode.ACCOUNT_ALREADY_REGISTER);
            }
        }

        comSmsService.sendMsgCode(phone, SmsConst.Type.COMMON.value, imgCode, request, landCode, sendType);
        return Response.success();
    }

    /**
     * 落地页获取图片验证码
     */
    @LimitLess
    @RequestMapping("/download/codeImg")
    public void getCodeImg(String phone, String landCode, HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtil.getIp(request);
        String lhbk_uuid = CookieKit.getCookieAttr("lhbk_uuid", request);
        logger.info("调用获取验证码>>>>,landCode={},phone={},lhbk_uuid={},imageCode={},ip={}", landCode, phone, lhbk_uuid, lhbk_uuid, ip);

        Assert.hasLength(phone, LocaleKit.get("common.param.notnull", "phone"));
        Assert.hasLength(landCode, LocaleKit.get("common.param.notnull", "landCode"));
        String host = HeaderKit.getHost(request);
        if (!host.contains(":")) {
            host = host + ":";
        }
        imgAuthHelper.writeMsgCode(response, host + landCode + phone, SmsConst.Type.COMMON.code);
    }

    /**
     * 落地页判断是否需要图片验证码
     */
    @LimitLess
    @RequestMapping("/check/need")
    public Response checkNeddImgCode(String phone, String landCode, HttpServletRequest request, HttpServletResponse response) {

        Assert.hasLength(phone, LocaleKit.get("common.param.notnull", "phone"));
        Assert.hasLength(landCode, LocaleKit.get("common.param.notnull", "landCode"));


        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }
        String redisKey = UserRedisKey.REDIS_MSG_LIMIT_KEY + agencyId + "_" + phone;
        Object o = redisTemplate.opsForValue().get(redisKey);
        Ret ret = Ret.create();
        ret.put("need", o == null ? 1 : 0);
        return Response.success(ret);
    }


    /**
     * 落地页注册
     */
    @PostMapping("/enroll")
    @ResponseBody
    @LimitLess
    public Response enroll(UserRegisterPO registerPO, HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String lhbk_uuid = CookieKit.getCookieAttr("lhbk_uuid", request);
        logger.info("调用落地页注册>>>>,landCode={},phone={},lhbk_uuid={},imageCode={},ip={}", registerPO.getLandCode(), registerPO.getPhone(), lhbk_uuid, registerPO.getImgCode(), ip);

        Assert.notNull(registerPO, LocaleKit.get("common.param.error"));
        Assert.hasLength(registerPO.getPhone(), LocaleKit.get("msg.2055", "phone"));
        Assert.hasLength(registerPO.getMsgCode(), LocaleKit.get("common.param.notnull", "msgCode"));
        Assert.hasLength(registerPO.getLandCode(), LocaleKit.get("common.param.notnull", "landCode"));

        if (!Regular.checkPhone(registerPO.getPhone())) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }

        String host = HeaderKit.getHost(request);
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        if (checkAgencyRegister(agencyId)) {
            throw new BusinessException(UserRespCode.AGENCY_NOT_REGISTER);
        }

        String existRedis = UserRedisKey.REDIS_MSG_LIMIT_KEY + agencyId + "_" + registerPO.getPhone();
        Integer count = (Integer) redisTemplate.opsForValue().get(existRedis);

        if (count != null && count > 1) {
            Assert.hasLength(registerPO.getImgCode(), LocaleKit.get("common.param.notnull", "imgCode"));
        }

        String redisKey = UserRedisKey.REDIS_KEY_USER_REGISTER + ":" + agencyId + ":" + registerPO.getPhone();
        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
            throw new BusinessException(UserRespCode.REQUEST_MORE);
        }
        try {

            redisTemplate.expire(redisKey, 5, TimeUnit.SECONDS);

            Integer vestId = null;
            AgencyChannelModel channel = null;
            if (StringUtil.isNotEmpty(registerPO.getLandCode())) {
                channel = comAgencyService.findOneChannelByLandCode(registerPO.getLandCode(), agencyId);
                if (channel != null) {
                    vestId = channel.getVestId();
                }
            }
            if (vestId == null) {
                logger.info("马甲id,不存在,vestId={}");
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }

            AppVestModel vestModel = comAgencyService.findOneVestByIdOrVestCode(agencyId, vestId, null);
            if (vestModel == null) {
                logger.info("马甲不存在,vestId={},agencyId={}", vestId, agencyId);
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }


            logger.info("落地页注册>>>>,agencyId={},landCode={},phone={},lhbk_uuid={},imageCode={},vestCode={},ip={}", agencyId, registerPO.getLandCode(), registerPO.getPhone(), lhbk_uuid, registerPO.getImgCode(), vestModel.getVestCode(), ip);

            //校验验证码
            if (!comSmsService.checkMsgCodeForPhone(vestModel, registerPO.getPhone(), registerPO.getMsgCode(), SmsConst.Type.COMMON)) {
                throw new BusinessException(UserRespCode.AUTH_MSG_CODE_FAIL, LocaleKit.resolverOrGet(LocaleKit.MSG_PREFIX + VLIDATE_ERROR_REPEAT));
            }
            if (!host.contains(":")) {
                host = host + ":";
            }
            if (count != null && count > 1) {
                // 校验图片验证码
                if (!imgAuthHelper.checkImgCode(host + registerPO.getLandCode() + registerPO.getPhone() + ":" + registerPO.getImgCode(), SmsConst.Type.COMMON.code, true)) {

                    throw new BusinessException(UserRespCode.AUTH_IMG_CODE_FAIL);
                }
            }

            UserModel user = new UserModel();

            user.setPhone(StringUtil.trimToNull(registerPO.getPhone()));
            user.setLoginIp(ip);
            user.setRegistrationIp(ip);
            user.setAgencyId(agencyId);
            user.setThirdId(IdWorker.get32UUID());
            user.setVestCode(vestModel.getVestCode());
            user.setVestId(vestModel.getId());
            user.setChannelId(channel.getId());
            user.setChannelCode(channel.getChannelCode());


            UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(registerPO.getPhone(), vestId, agencyId);
            if (dbModel != null) {
                throw new BusinessException(UserRespCode.ACCOUNT_ALREADY_REGISTER);
            }
            //注册
            userOptService.enroll(user);
        } finally {
            redisTemplate.delete(redisKey);
        }
        return Response.success();
    }


    /**
     * 描述:检查手机号是否存在
     */
    @GetMapping("/checkExist")
    @ResponseBody
    @LimitLess
    public Response checkExist(String phone, HttpServletRequest request) {
        Assert.hasLength(phone, LocaleKit.get("common.param.notnull", "phone"));
        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }
        String vestCode = PlatformKit.parseChannel(request);
        AppVestModel vest = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vest == null) {
            logger.info("马甲不存在,vestCode={},agencyId={}", vestCode, agencyId);
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(phone, vest.getId(), agencyId);
        Map<String, Object> map = new HashMap<>();
        map.put("exist", dbModel == null ? 0 : 1);
        return Response.success(map);
    }

    /**
     * 描述:检查手机号是否存在
     */
    @GetMapping("/checkSendVoice")
    @ResponseBody
    @LimitLess
    public Response checkSendVoice(HttpServletRequest request) {

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        SmsVoiceModel voiceModel = comSmsVoiceSenderDao.findAgencySenderVoice(agencyId);
        Map<String, Object> map = new HashMap<>();
        map.put("send", voiceModel == null ? 0 : 1);
        return Response.success(map);
    }


    @PostMapping("/register")
    @ResponseBody
    @LimitLess
    public Response register(UserRegisterPO registerPO, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(registerPO, LocaleKit.get("common.param.error"));

        Assert.hasLength(registerPO.getPhone(), LocaleKit.get("msg.2055", "phone"));
        Assert.hasLength(registerPO.getMsgCode(), LocaleKit.get("common.param.notnull", "msgCode"));
        if (!Regular.checkPhone(registerPO.getPhone())) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }

        Assert.hasLength(registerPO.getUserPass(), LocaleKit.get("common.param.notnull", "userPass"));

        String ip = IpUtil.getIp(request);
        String host = HeaderKit.getHost(request);

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        //校验代理是否允许注册
        if (checkAgencyRegister(agencyId)) {
            throw new BusinessException(UserRespCode.AGENCY_NOT_REGISTER);
        }

        registerPO.setAgencyId(agencyId);

        logger.info(">>>>user register host={},,agencyId={}", host, agencyId);

        String redisKey = UserRedisKey.REDIS_KEY_USER_REGISTER + ":" + agencyId + ":" + registerPO.getPhone();
        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
            throw new BusinessException(UserRespCode.REQUEST_MORE);
        }
        try {

            redisTemplate.expire(redisKey, 5, TimeUnit.SECONDS);

            String vestCode = PlatformKit.parseChannel(request);

            AppVestModel vestModel = null;
            if (StringUtil.isNotEmpty(vestCode)) {
                vestModel = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
            }
            if (vestModel == null) {
                logger.info("马甲不存在,vestCode={},agencyId={}", vestCode, agencyId);
                throw new BusinessException(UserRespCode.PERMISSION_DENY);
            }
            //校验验证码
            if (!comSmsService.checkMsgCodeForPhone(vestModel, registerPO.getPhone(), registerPO.getMsgCode(), SmsConst.Type.COMMON)) {
                throw new BusinessException(UserRespCode.AUTH_MSG_CODE_FAIL, LocaleKit.resolverOrGet(LocaleKit.MSG_PREFIX + VLIDATE_ERROR_REPEAT));
            }

            UserModel user = createUserModel(registerPO, ip, request);
            String market = PlatformKit.parseMarket(request);
            if (StringUtil.isNotEmpty(market)) {
                String[] split = market.split(":");
                if (split.length >= 2) {
                    user.setPlatform(split[0]);
                    user.setChannelCode(split[1]);
                }
            }
            user.setVestId(vestModel.getId());
            user.setVestCode(vestModel.getVestCode());
            UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(registerPO.getPhone(), vestModel.getId(), agencyId);
            if (dbModel != null) {
                throw new BusinessException(UserRespCode.ACCOUNT_ALREADY_REGISTER);
            }
            //注册
            user = userOptService.register(user);

            //这里直接登录需要设置登录属性
            TokenUtil.generateToken(user.getId(), request, response);
            //logService.save(user.getId(), ip, HeaderKit.getUserAgent(request), UserConstant.Opt.LOGIN);
            //渠道扣量统计
            jmsTemplate.convertAndSend(JmsDest.CHANNEL_DEDUCTION_STATIS, user);

        } catch (Exception e) {
            logger.error("quickLogin error:{}.registerPO={}", e, registerPO);
            throw new BusinessException(UserRespCode.USER_REGISTER_FAIL, null, e);
        } finally {
            redisTemplate.delete(redisKey);
        }
        return Response.success();
    }


    @PostMapping("/quickLogin")
    @LimitLess
    public Response quickLogin(UserRegisterPO registerPO, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(registerPO, LocaleKit.get("common.param.error"));
        Assert.hasLength(registerPO.getPhone(), LocaleKit.get("msg.2055", "phone"));
        Assert.hasLength(registerPO.getMsgCode(), LocaleKit.get("common.param.notnull", "msgCode"));

        if (!Regular.checkPhone(registerPO.getPhone())) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }

        String ip = IpUtil.getIp(request);

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        registerPO.setAgencyId(agencyId);
        if (logger.isInfoEnabled())
            logger.info(">>>quickLogin agencyId={}", agencyId);
        UserModel user = createUserModel(registerPO, ip, request);

        String redisKey = REDIS_KEY + ":" + agencyId + ":" + registerPO.getPhone();

        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
            throw new BusinessException(UserRespCode.REQUEST_MORE);
        }
        redisTemplate.expire(redisKey, 5, TimeUnit.SECONDS);

        String market = PlatformKit.parseMarket(request);
        if (StringUtil.isNotEmpty(market)) {
            String[] split = market.split(":");
            if (split.length >= 2) {
                registerPO.setPlatform(split[0]);
                registerPO.setMarket(split[1]);
            }
        }

        String vestCode = PlatformKit.parseChannel(request);
        AppVestModel vest = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vest == null) {
            logger.info("马甲不存在,vestCode={},agencyId={}", vestCode, agencyId);
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }
        try {

            UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(registerPO.getPhone(), vest.getId(), agencyId);

            if (dbModel == null) {
                throw new BusinessException(UserRespCode.ACCOUNT_NOT_REGISTER);
            }
            if (!comSmsService.checkMsgCodeForPhone(vest, registerPO.getPhone(), registerPO.getMsgCode(), SmsConst.Type.COMMON)) {
                throw new BusinessException(UserRespCode.AUTH_MSG_CODE_FAIL, LocaleKit.resolverOrGet(LocaleKit.MSG_PREFIX + VLIDATE_ERROR_REPEAT));
            }
            if (StringUtil.isEmpty(dbModel.getChannelCode())) {
                user.setChannelCode(registerPO.getMarket());
            }

            user.setId(dbModel.getId());
            user.setVestId(dbModel.getVestId());
            user.setUserPass(null);
            user.setPhone(null);
            user.setRegistrationIp(null);
            user.setAgencyId(null);
            user.setLoginNum(dbModel.getLoginNum() + 1);

            userOptService.quickLogin(user, vest);

            if (dbModel.getLastLoginTime() == null) {
                // 用户注册登录统计
                UserStatisticDTO dto = new UserStatisticDTO();
                dto.setRegisterDate(Date8Util.date2LocalDate(dbModel.getCreateTime()));
                dto.setIsLogin(CommonConst.YES);
                dto.setChannelCode(dbModel.getChannelCode());
                dto.setActionDate(LocalDate.now());
                dto.setVestId(user.getVestId());
                dto.setAgencyId(dbModel.getAgencyId());
                jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
                logger.info(">>>>发送统计,用户(注册)行为(登录)jms ,data={}", dto);
            } else {
                LocalDate now = LocalDate.now();
                LocalDate lastLoginDate = Date8Util.date2LocalDate(dbModel.getLastLoginTime());
                if (lastLoginDate.isBefore(now)) {
                    UserStatisticDTO dto = new UserStatisticDTO();
                    dto.setRegisterDate(Date8Util.date2LocalDate(dbModel.getCreateTime()));
                    dto.setIsLogin(CommonConst.YES);
                    dto.setChannelCode(dbModel.getChannelCode());
                    dto.setActionDate(LocalDate.now());
                    dto.setVestId(user.getVestId());
                    dto.setIsActionLogin(CommonConst.YES);
                    dto.setAgencyId(dbModel.getAgencyId());
                    jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
                    logger.info(">>>>发送统计,用户行为(登录)jms ,data={}", dto);
                }

            }


            TokenUtil.generateToken(dbModel.getId(), request, response);

//            logService.save(dbModel.getId(), ip, request.getHeader("User-Agent"), UserConstant.Opt.LOGIN);
            return Response.success();

        } finally {
            redisTemplate.delete(redisKey);
        }
    }

    @PostMapping("/login")
    @LimitLess
    public Response login(UserLoginPO loginPO, HttpServletRequest request, HttpServletResponse response) {
        if (loginPO == null) {
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }
        Assert.hasLength(loginPO.getPhone(), LocaleKit.get("msg.2055", "phone"));
        Assert.hasLength(loginPO.getUserPass(), LocaleKit.get("common.param.notnull", "userPass"));

        loginPO.setIp(IpUtil.getIp(request));

        String market = PlatformKit.parseMarket(request);
        if (StringUtil.isNotEmpty(market)) {
            String[] split = market.split(":");
            if (split.length >= 2) {
                loginPO.setMarket(split[1]);
                loginPO.setPlatform(split[0]);
            }
        }
        loginPO.setAppVersion(PlatformKit.getVersion(request));

        //取请求域名
        String host = HeaderKit.getHost(request);

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        loginPO.setAgencyId(agencyId);
        if (logger.isInfoEnabled())
            logger.info(">>>>user login host={},agencyId={}", host, agencyId);


        String vestCode = PlatformKit.parseChannel(request);
        AppVestModel vest = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vest == null) {
            logger.info("马甲不存在,vestCode={},agencyId={}", vestCode, agencyId);
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }


        UserModel userModel = userOptService.login(loginPO, vest);
        if (userModel != null) {
            TokenUtil.generateToken(userModel.getId(), request, response);

//            String browserVersion = request.getHeader("User-Agent");
//            logService.save(userModel.getId(), loginPO.getIp(), browserVersion, UserConstant.Opt.LOGIN);

        }
        return Response.success();
    }

    @PostMapping("/resetPass")
    @LimitLess
    public Response resetPass(String userPass, String phone, String msgCode, HttpServletRequest request) {
        Assert.hasLength(userPass, LocaleKit.get("common.param.notnull", "userPass"));
        Assert.hasLength(phone, LocaleKit.get("common.param.notnull", "phone"));
        Assert.hasLength(msgCode, LocaleKit.get("common.param.notnull", "msgCode"));

        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        if (agencyId == null) {
            logger.info("没有找到代理id");
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }

        String vestCode = PlatformKit.parseChannel(request);
        AppVestModel vest = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vest == null) {
            logger.info("马甲不存在,vestCode={},agencyId={}", vestCode, agencyId);
            throw new BusinessException(UserRespCode.PERMISSION_DENY);
        }


        if (!comSmsService.checkMsgCodeForPhone(vest, phone, msgCode, SmsConst.Type.COMMON)) {
            throw new BusinessException(UserRespCode.AUTH_MSG_CODE_FAIL, LocaleKit.resolverOrGet(LocaleKit.MSG_PREFIX + VLIDATE_ERROR_REPEAT));
        }

        UserModel dbModel = comUserService.findUserModelByAgencyIdAndPhone(phone, vest.getId(), agencyId);
        if (dbModel == null) {
            throw new ResolveMsgException(UserRespCode.AUTH_NOT_EXIST, LocaleKit.get(LocaleKit.MSG_PREFIX + ACCOUNT_NOT_EXISTS));
        }
        dbModel.setUserPass(userPass);
        userOptService.resetLoginPassByUserId(dbModel);
        return Response.success();
    }

    @PostMapping("/logout")
    public Response Logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String token1 = null;
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(TokenUtil.TOKEN1, cookie.getName())) {
                token1 = cookie.getValue();
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            if (StringUtils.equals(TokenUtil.TOKEN2, cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        if (token1 != null) {
            token1 = new String(Base64.decodeBase64(token1.getBytes()));
            redisTemplate.delete(UserUtil.SESSION_USER_ID_PREFIX + token1);
        }
        return Response.success();
    }

    /**
     * 渠道uv统计
     */
    @PostMapping("/channelUvStatis")
    @LimitLess
    public Response channelUvStatis(HttpServletRequest request, String landCode) {
        String ip = IpUtil.getIp(request);
        String lhbk_uuid = CookieKit.getCookieAttr("lhbk_uuid", request);

        logger.info(">>>>调用落地页UVlandCode={},lhbk_uuid={},ip={}", landCode, lhbk_uuid, ip);

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        AgencyChannelModel channel = comAgencyService.findChannelByLandCode(landCode, agencyId);

        if (channel == null) {
            return Response.success();
        }

        if (StringUtils.isBlank(ip) || agencyId == null || channel.getId() == null) Response.success();

//        String redisKey = UserRedisKey.CHANNEL_UV_STATIS + channel.getId() + ":" + ip;
//        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
//            Response.success();
//        }
//        logger.info(">>>>落地页UV,agencyId={},landCode={},lhbk_uuid={},ip={}", landCode, lhbk_uuid, ip);


        UserUvModel uvModel = new UserUvModel();
        uvModel.setIp(ip);
        uvModel.setChannelId(channel.getId());
        uvModel.setUa(lhbk_uuid);
        uvModel.setChannelCode(channel.getChannelCode());
        uvModel.setAgencyId(agencyId);
        uvModel.setVestId(channel.getVestId());
        userUvDao.saveUv(uvModel);


        UserStatisticDTO dto = new UserStatisticDTO();
        dto.setAgencyId(agencyId);
        dto.setChannelCode(channel.getChannelCode());
        dto.setActionDate(LocalDate.now());
        dto.setRegisterDate(Date8Util.date2LocalDate(new Date()));
        dto.setIsUv(CommonConst.YES);
        dto.setVestId(channel.getVestId());
        jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
        return Response.success();
    }


    private UserModel createUserModel(UserRegisterPO registerPO, String ip, HttpServletRequest request) {
        UserModel user = new UserModel();

        user.setDeviceId(registerPO.getDeviceId());
        user.setMobileName(registerPO.getMobileName());
        user.setMobileType(registerPO.getMobileType());
        user.setSystemVersion(registerPO.getSystemVersion());
        user.setGexing(registerPO.getGexing());

        user.setAppVersion(PlatformKit.getVersion(request));

        user.setUserPass(registerPO.getUserPass());
        user.setPhone(StringUtil.trimToNull(registerPO.getPhone()));
        user.setLoginIp(ip);
        user.setRegistrationIp(ip);
        user.setAgencyId(registerPO.getAgencyId());
        return user;
    }

    private boolean checkAgencyRegister(Integer agencyId) {
        String agencyIds = properties.getString("user:agency:validate");
        if (StringUtils.isNotBlank(agencyIds)) {
            List<Integer> ids = Arrays.stream(agencyIds.split(",")).map(s -> Integer.parseInt(s.trim())).
                    collect(Collectors.toList());
            if (ids.contains(agencyId)) {
                logger.info(">>>>agencyId={} 暂停注册", agencyId);
                return true;
            }

        }
        return false;
    }

}
