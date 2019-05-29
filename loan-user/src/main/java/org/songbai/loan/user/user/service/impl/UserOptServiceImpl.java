package org.songbai.loan.user.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.encrypt.PasswordEncryptUtil;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.UserDao;
import org.songbai.loan.user.user.model.po.UserLoginPO;
import org.songbai.loan.user.user.service.UserEncryptService;
import org.songbai.loan.user.user.service.UserOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.songbai.loan.constant.resp.UserRespCode.ACCOUNT_NOT_EXISTS;
import static org.songbai.loan.constant.resp.UserRespCode.ACCOUNT_PASS_NOTSET;

/**
 * Author: qmw
 * Date: 2018/10/30 下午12:57
 */
@Service
public class UserOptServiceImpl implements UserOptService {
    private Logger logger = LoggerFactory.getLogger(UserOptServiceImpl.class);
    @Autowired
    private UserEncryptService userEncryptService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private SpringProperties properties;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private UserDao userDao;

    @Override
    public UserModel register(UserModel user) {
        UserModel userPas = userEncryptService.encrypt(user.getUserPass());
        user.setUserPass(userPas.getUserPass());
        user.setPassSalt(userPas.getPassSalt());
        user.setThirdId(IdWorker.get32UUID());
        user.setPassEncryptTimes(userPas.getPassEncryptTimes());
        user.setPassEncryptTimes(userPas.getPassEncryptTimes());
        userDao.insert(user);
        //redisTemplate.opsForHash().put(UserRedisKey.USER_INFO, user.getId(), user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("agencyId", user.getAgencyId());
        jmsTemplate.convertAndSend(JmsDest.CREATE_USER_INFO, jsonObject);

        if (StringUtil.isNotEmpty(user.getGexing()) && user.getId() != null) {
            userDao.updateOtherUserGexing(user.getGexing(), user.getId());
        }

        if (StringUtil.isNotEmpty(user.getDeviceId())) {
            JSONObject device = new JSONObject();
            device.put("userId", user.getId());
            device.put("device", user.getDeviceId());
            jmsTemplate.convertAndSend(JmsDest.INSERT_DEVICE, device);
        }

        // 用户注册登录统计
        UserStatisticDTO dto = new UserStatisticDTO();
        dto.setRegisterDate(LocalDate.now());
        dto.setAgencyId(user.getAgencyId());
        dto.setIsLogin(CommonConst.YES);
        dto.setIsRegister(CommonConst.YES);
        dto.setChannelCode(user.getChannelCode());
        dto.setActionDate(LocalDate.now());
        dto.setVestId(user.getVestId());
        jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
        logger.info(">>>>发送统计,用户行为(登录+注册)jms ,data={}", dto);

        return user;
    }

    @Override
    public UserModel login(UserLoginPO loginPO, AppVestModel vest) {
        UserModel userModel = new UserModel();

        userModel.setPhone(StringUtils.trimToNull(loginPO.getPhone()));
        userModel.setAgencyId(loginPO.getAgencyId());
        userModel = comUserService.findUserModelByAgencyIdAndPhone(loginPO.getPhone(), vest.getId(), loginPO.getAgencyId());
        if (userModel != null) {
            if (StringUtils.isEmpty(userModel.getUserPass())) {
                throw new BusinessException(ACCOUNT_PASS_NOTSET);
            }
            Integer loginErrorNum = userModel.getLoginErrorNum();
            Integer loginErrorMax = properties.getInteger("user.login_error_num", 5);
            Integer loginTimesLeft = loginErrorMax - loginErrorNum;

            String redisLoginLimitKey = UserRedisKey.LOGIN_LIMIT + ":" + vest.getVestCode() + ":" + loginPO.getPhone();

            String redisLoginLimit = String.valueOf(redisTemplate.opsForValue().get(redisLoginLimitKey));

            if (!redisLoginLimit.equals("null")) {
                Long timeLimit = redisTemplate.getExpire(redisLoginLimitKey, TimeUnit.SECONDS);
                if (timeLimit / 60 > 0) {
                    logger.info("登录:账号{}剩余登录限制{}分钟", loginPO.getPhone(), timeLimit);
                    throw new ResolveMsgException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2040", timeLimit / 60));

                } else {
                    if (timeLimit % 60 > 0) {
                        logger.info("登录:账号{}剩余登录限制{}秒", loginPO.getPhone(), timeLimit);
                        throw new ResolveMsgException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2043", timeLimit % 60));
                    }

                }
            } else if (loginTimesLeft == 0) {
                loginErrorNum = 0;
                loginTimesLeft = loginErrorMax;
            }

            String errorTimesKey = UserRedisKey.USER_LOGIN_ERROR_TIMES + ":" + vest.getVestCode() + ":" + loginPO.getPhone();
            String encryptPass = PasswordEncryptUtil.digest(loginPO.getUserPass(), userModel.getPassSalt(), userModel.getPassEncryptTimes());

            UserModel param = new UserModel();
            param.setId(userModel.getId());

            if (!StringUtils.equals(userModel.getUserPass(), encryptPass)) {
                if (loginTimesLeft == 1) {
                    int timeLimit = properties.getInteger("user.login_poor_time", 5) * 60;
                    redisTemplate.opsForValue().set(redisLoginLimitKey, "", timeLimit, TimeUnit.SECONDS);
                }
                param.setLoginErrorNum(++loginErrorNum);


                userDao.updateById(param);
                logger.info("登录:账号{}剩余登录{}次", loginPO.getPhone(), loginTimesLeft - 1);

                //增加用户登录错误次数
                redisTemplate.opsForValue().increment(errorTimesKey, 1L);
                throw new ResolveMsgException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2044", (--loginTimesLeft)));
            } else {
                redisTemplate.delete(redisLoginLimitKey);
                if (userModel.getLoginNum() <= 0 && StringUtil.isEmpty(userModel.getChannelCode())) {
                    param.setChannelCode(loginPO.getMarket());
                }
                param.setLoginErrorNum(0);
                param.setLoginNum(userModel.getLoginNum() + 1);
                param.setLastLoginTime(new Date());
                param.setPlatform(loginPO.getPlatform());
                param.setDeviceId(loginPO.getDeviceId());
                param.setLoginIp(loginPO.getIp());
                param.setMobileName(loginPO.getMobileName());
                param.setMobileType(loginPO.getMobileType());
                param.setSystemVersion(loginPO.getSystemVersion());
                param.setAppVersion(loginPO.getAppVersion());
                param.setGexing(loginPO.getGexing());

                userDao.updateById(param);

                //清除用户登录错误次数
                redisTemplate.delete(errorTimesKey);

                redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, param.getId());
                logger.info("登录:账号为{}的用户登录成功", loginPO.getPhone());

                if (StringUtil.isNotEmpty(param.getGexing()) && param.getId() != null) {
                    userDao.updateOtherUserGexing(param.getGexing(), param.getId());
                }

                if (StringUtil.isNotEmpty(loginPO.getDeviceId())) {
                    JSONObject device = new JSONObject();
                    device.put("userId", userModel.getId());
                    device.put("device", loginPO.getDeviceId());
                    jmsTemplate.convertAndSend(JmsDest.INSERT_DEVICE, device);
                }
                sendLoginMsg(userModel);

                return userModel;
            }
        } else {
            logger.info("登录:账号{}未注册", loginPO.getPhone());

            throw new ResolveMsgException(UserRespCode.AUTH_NOT_EXIST, LocaleKit.get(LocaleKit.MSG_PREFIX + ACCOUNT_NOT_EXISTS));
        }
    }

    @Override
    public void quickLogin(UserModel dbModel, AppVestModel vest) {


        dbModel.setLoginErrorNum(0);
        dbModel.setLastLoginTime(new Date());
        userDao.updateById(dbModel);

        String errorTimesKey = UserRedisKey.USER_LOGIN_ERROR_TIMES + ":" + vest.getVestCode() + ":" + dbModel.getPhone();
        redisTemplate.delete(errorTimesKey);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, dbModel.getId());

        if (StringUtil.isNotEmpty(dbModel.getGexing()) && dbModel.getId() != null) {
            userDao.updateOtherUserGexing(dbModel.getGexing(), dbModel.getId());
        }

        if (StringUtil.isNotEmpty(dbModel.getDeviceId())) {
            JSONObject device = new JSONObject();
            device.put("userId", dbModel.getId());
            device.put("device", dbModel.getDeviceId());
            jmsTemplate.convertAndSend(JmsDest.INSERT_DEVICE, device);
        }

    }

    /**
     * 发送用户登录jms
     *
     * @param dbModel
     */
    private void sendLoginMsg(UserModel dbModel) {
        if (dbModel.getLastLoginTime() == null) {
            // 用户注册登录统计
            UserStatisticDTO dto = new UserStatisticDTO();
            dto.setRegisterDate(Date8Util.date2LocalDate(dbModel.getCreateTime()));
            dto.setAgencyId(dbModel.getAgencyId());
            dto.setIsLogin(CommonConst.YES);
            dto.setChannelCode(dbModel.getChannelCode());
            dto.setActionDate(LocalDate.now());
            dto.setVestId(dbModel.getVestId());
            jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
            logger.info(">>>>发送统计,用户行为(登录)jms ,data={}", dto);

        }
    }

    @Override
    public void resetLoginPassByUserId(UserModel dbModel) {
        UserModel userPas = userEncryptService.encrypt(dbModel.getUserPass());
        userPas.setId(dbModel.getId());
        userPas.setLoginErrorNum(0);
        userDao.updateById(userPas);
        logger.info("更新密码:密码修改成功");
    }

    @Override
    public void saveUser(UserModel user) {
        userDao.insert(user);
        redisTemplate.opsForHash().put(UserRedisKey.USER_INFO, user.getId(), user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("agencyId", user.getAgencyId());
        jmsTemplate.convertAndSend(JmsDest.CREATE_USER_INFO, jsonObject);

        if (StringUtil.isNotEmpty(user.getDeviceId())) {
            JSONObject device = new JSONObject();
            device.put("userId", user.getId());
            device.put("device", user.getDeviceId());
            jmsTemplate.convertAndSend(JmsDest.INSERT_DEVICE, device);
        }
    }

    @Override
    public void enroll(UserModel user) {
        userDao.insert(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("agencyId", user.getAgencyId());
        jmsTemplate.convertAndSend(JmsDest.CREATE_USER_INFO, jsonObject);

        UserStatisticDTO dto = new UserStatisticDTO();
        dto.setRegisterDate(Date8Util.date2LocalDate(new Date()));
        dto.setAgencyId(user.getAgencyId());
        dto.setIsRegister(CommonConst.YES);
        dto.setChannelCode(user.getChannelCode());
        dto.setActionDate(LocalDate.now());
        dto.setVestId(user.getVestId());
        jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
        logger.info(">>>>发送统计,用户行为(注册)jms ,data={}", dto);

        //发送渠道扣量统计
        jmsTemplate.convertAndSend(JmsDest.CHANNEL_DEDUCTION_STATIS, user);
    }
}
