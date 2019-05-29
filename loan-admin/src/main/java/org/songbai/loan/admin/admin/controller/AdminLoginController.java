package org.songbai.loan.admin.admin.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/login")
public class AdminLoginController {
    Logger logger = LoggerFactory.getLogger(AdminLoginController.class);
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    AdminUrlAccessResourceService adminUrlAccessResourceService;
    @Autowired
    AdminRoleService adminRoleService;
    @Value("${admin.user.pass_encrypt_times}")
    private int times;
    @Autowired
    AdminMenuResouceService adminMenuResouceService;
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    ComAgencyService comAgencyService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SpringProperties properties;
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 登录验证
     */
    @RequestMapping(value = "/verification")
    @ResponseBody
    @LimitLess
    public Response verification(String userAccount, String password, String msgCode,
                                 HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(userAccount, "请输入登录账号");
        Assert.notNull(password, "请输入密码");
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        AdminUserModel userModel = adminUserService.getUserByUserAccountPassword(userAccount, null, agencyId, null);
        if (userModel == null) {
            return Response.response(RespCode.SERVER_ERROR, "该用户不存在");
        }

        Integer passEncryptTimes = userModel.getPassEncryptTimes();
        Long accountLimitTime = userModel.getAccountLimitTime();
        if (passEncryptTimes >= times && accountLimitTime > System.currentTimeMillis()) {
            return Response.error(
                    "该用户被禁止登录,请在" + ((accountLimitTime - System.currentTimeMillis()) / (1000 * 60) + 1) + "分钟后登录");
        }

        if (!userModel.getPassword().equals(AdminUserModel.handlePassword(password))) {
            passEncryptTimes = passEncryptTimes + 1;
            userModel.setPassEncryptTimes(passEncryptTimes);
            if (passEncryptTimes >= times) {
                userModel.setAccountLimitTime(
                        (long) (System.currentTimeMillis() + 1000 * 60 * (Math.pow(2, passEncryptTimes - times))));
            }
            adminUserService.updateAdminUserExceptPassword(userModel);
            return Response.response(RespCode.SERVER_ERROR, "用户密码错误,当前错误" + passEncryptTimes + "次");
        }
        String ip = IpUtil.getIp(request);
        String host = HeaderKit.getHost(request);
        String ua = HeaderKit.getUserAgent(request);
        String redisKey = "admin:user:login:validate:" + ip + ":" + host + ":" + ua;

        if (userModel.getIsValidate() != CommonConst.DELETED_NO && redisTemplate.opsForValue().get(redisKey + userAccount) == null) {
            if (StringUtils.isBlank(msgCode)) {
                throw new BusinessException(AdminRespCode.AUTH_MSG_CODE_NEED);
            }
            // 短信短信验证码验证功能
            if (!checkMsgCode(userModel.getDataId(), userModel.getPhone(), msgCode)) {
                throw new BusinessException(AdminRespCode.AUTH_MSG_CODE_FAIL);
            }


            redisTemplate.opsForValue().increment(redisKey + userAccount, 1);
            Date currentTime = new Date();
            long end = DateUtils.addDays(DateUtils.truncate(currentTime, Calendar.DAY_OF_MONTH), 1).getTime();

            redisTemplate.expire(redisKey + userAccount, end - currentTime.getTime(), TimeUnit.MILLISECONDS);
        }

        logger.info("用户{}登录密码{}", userModel.getName(), password);
        userModel.setAccountLimitTime(0L);
        userModel.setPassEncryptTimes(0);
        adminUserService.updateAdminUserExceptPassword(userModel);


        if (userModel.isDisable()) {
            return Response.response(RespCode.SERVER_ERROR, "该用户已禁用，请联系管理员");
        }


        adminUserHelper.login(request, response, userModel);

        return Response.success(userModel.getResourceType());
    }

    @RequestMapping(value = "/safe_userMession")
    @ResponseBody
    public Response userMession(HttpServletRequest request) {
        return Response.success(adminUserService.getUserMession(request));
    }

    @GetMapping(value = "/safe_checkUserValid")
    @ResponseBody
    @LimitLess
    public Response checkUserValid(String userAccount, HttpServletRequest request) {
        Assert.notNull(userAccount, "请输入登录账号");

        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        AdminUserModel userModel = adminUserService.getUserByUserAccountPassword(userAccount, null, agencyId, null);
        if (userModel == null) {
            return Response.response(RespCode.SERVER_ERROR, "该用户不存在");
        }
        Integer validate = userModel.getIsValidate();

        String ip = IpUtil.getIp(request);
        String host = HeaderKit.getHost(request);
        String ua = HeaderKit.getUserAgent(request);
        String redisKey = "admin:user:login:validate:" + ip + ":" + host + ":" + ua;

        if (redisTemplate.opsForValue().get(redisKey + userAccount) != null) {
            validate = 0;
        }
        Ret ret = Ret.create();
        ret.put("isValidate", validate);
        return Response.success(ret);
    }

    /**
     * 退出系统 清除session和用户Id数据
     */
    @RequestMapping(value = "/safe_loginOut")
    @ResponseBody
    public Response loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        adminUserHelper.logout(request, response);

        return Response.success();
    }

    @RequestMapping(value = "/responseRedirect")
    public void responseRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getRequestDispatcher("login.html").forward(request, response);
    }

    @RequestMapping(value = "/sendSmsCode")
    @ResponseBody
    public Response sendSmsCode(String userAccount, HttpServletRequest request) {

        if (StringUtil.isEmpty(userAccount)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "账号不能为空");
        }
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        // v2 用户登录后台重新
        AdminUserModel userModel = adminUserService.getUserByUserAccountPassword(userAccount, null, agencyId, null);
        if (userModel == null) {
            return Response.response(RespCode.SERVER_ERROR, "该用户不存在");
        }

        if (StringUtil.isEmpty(userModel.getPhone()) || !Regular.checkPhone(userModel.getPhone())) {
            return Response.error("没有绑定手机验证码或者绑定手机号码有误，请联系管理员");
        }

        String ip = IpUtil.getIp(request);

        sendMsgCode(userModel.getDataId(), userModel.getPhone(), ip);

        return Response.success(userModel.getPhone().replaceAll("(1[2-9][0-9])([0-9]{4})([0-9]{4})", "$1****$3"));
    }


    private boolean checkMsgCode(Integer agencyId, String phone, String msgCode) {
        String redisMsgCodeKey = "admin:valid:sms:" + agencyId + ":" + phone;


        String smsDev = properties.getString("user.validate.sms.dev");
        if (StringUtils.isNotEmpty(smsDev) && smsDev.equalsIgnoreCase(msgCode)) {
            return true;
        }
        String oldMsgCode = null;
        if (redisTemplate.opsForValue().get(redisMsgCodeKey) != null) {
            oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey).toString();
        }

        if (logger.isInfoEnabled())
            logger.info("oldMsgCode:{},{}", oldMsgCode, redisMsgCodeKey);
        return oldMsgCode != null && oldMsgCode.equals(msgCode);

    }


    private boolean sendMsgCode(Integer agencyId, String phone, String ip) {

        String redisMsgCodeKey = "admin:valid:sms:" + agencyId + ":" + phone;
        String msgCodeLimit = "admin:msg_code:limit:" + phone;


        Object useCountObj = redisTemplate.opsForHash().get(msgCodeLimit, phone);
        Integer userCount = NumberUtils.toInt(useCountObj == null ? null : useCountObj + "");


        Integer dayMsgLimit = properties.getInteger("admin:msg:code:day:limit", 20);

        if (userCount > dayMsgLimit) {
            throw new BusinessException(UserRespCode.MSG_RAPPORT_CODE, LocaleKit.get("msg.2042", dayMsgLimit));
        }

        Long msgCodeExpireTime = properties.getLong("admin.user.login.msgCode_expire_time", 120L);
        msgCodeExpireTime *= 60;

        String oldMsgCode = null;
        if (redisTemplate.opsForValue().get(redisMsgCodeKey) != null) {
            oldMsgCode = redisTemplate.opsForValue().get(redisMsgCodeKey).toString();
        }
        if (StringUtils.isNotBlank(oldMsgCode)) {
            //每次发短信的间隔限制（分）
            Long msgCodeLimitTime = properties.getLong("admin.user.login.msgCode_limit_time", 120L);
            msgCodeLimitTime *= 60;
            //缓存剩余失效时间
            Long redisMsgCodeLeftTime = redisTemplate.getExpire(redisMsgCodeKey, TimeUnit.SECONDS);
            //每条短信的失效时间（分）1-(5 - 4 )
            Long msgCodeLimitTimeLeft = msgCodeLimitTime - (msgCodeExpireTime - redisMsgCodeLeftTime);
            if (msgCodeLimitTimeLeft > 0) {
                logger.info("手机号{}的短信验证码限制时间剩余{}秒,oldMsgCode={}", phone, msgCodeLimitTimeLeft, oldMsgCode);
                throw new BusinessException(UserRespCode.REPEAT_CODE, "短信验证码两小时内有效，请勿重复获取");
            }
        }

        String msgCode = String.valueOf(Math.random()).substring(2, 6);
        Object param = JSON.parse("{\"code\":\"" + msgCode + "\"}");

        redisTemplate.opsForHash().increment(msgCodeLimit, phone, 1);

        sendMsgByJms(ip, phone, param, agencyId);
        //设置失效时间
        redisTemplate.opsForValue().set(redisMsgCodeKey, msgCode, msgCodeExpireTime, TimeUnit.SECONDS);

        Date currentTime = new Date();
        long end = DateUtils.addDays(DateUtils.truncate(currentTime, Calendar.DAY_OF_MONTH), 1).getTime();

        redisTemplate.expire(msgCodeLimit, end - currentTime.getTime(), TimeUnit.MILLISECONDS);

        logger.info("{}的短信验证码{}已存入redis", redisMsgCodeKey, msgCode);
        return true;
    }

    /**
     * 发送短信
     */
    private void sendMsgByJms(String ip, String tele, Object param, Integer agencyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("ip", ip);
        map.put("phone", tele);
        map.put("param", param);
        map.put("smsType", SmsConst.Type.ADMIN_LOGIN.value);
        map.put("agencyId", 0);
        map.put("vestId", null);
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        logger.info("发送短信信息：message={}", message);
        jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);
    }

}
