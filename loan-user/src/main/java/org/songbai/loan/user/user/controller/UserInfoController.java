package org.songbai.loan.user.user.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.common.util.PhoneUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.UserDao;
import org.songbai.loan.user.user.dao.UserInfoDao;
import org.songbai.loan.user.user.model.po.UserInfoPo;
import org.songbai.loan.user.user.model.vo.UserVO;
import org.songbai.loan.user.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户【个人信息、身份证】认证
 *
 * @author wjl
 * @date 2018年10月30日 10:33:18
 * @description
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AuthenticationDao authDao;


    @PostMapping("/auth")
    public Response auth(@RequestParam("multipartFile") MultipartFile multipartFile, String type) {//type ：front正面  back反面
        if (multipartFile.isEmpty()) {
            throw new BusinessException(UserRespCode.UPLOAD_DATA_NULL);
        }
        Assert.hasText(type, "类型不能为空");
        Integer userId = UserUtil.getUserId();
        UserModel userModel = comUserService.selectUserModelById(userId);
        if (userModel.getDeleted() == CommonConst.YES){
            return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
        }
        return Response.success(userInfoService.idcardAuth(multipartFile, userId, type));
    }

    @PostMapping("/save")
    public Response save(UserInfoPo po, String type) {//type : idcard 身份证认证   info 个人信息认证
        Assert.hasText(type, "类型不能为空");
        Integer userId = UserUtil.getUserId();
        po.setUserId(userId);
        UserModel userModel = comUserService.selectUserModelById(po.getUserId());
        if (userModel == null) {
            throw new BusinessException(UserRespCode.USER_NOT_EXIST);
        }
        if (userModel.getDeleted() == CommonConst.YES){
            return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
        }
        AuthenticationModel authModel = authDao.selectById(userId);
        if (authModel == null) {
            throw new BusinessException(UserRespCode.DO_NOT_DELETE_TABLE);
        }
        UserInfoModel infoModel = new UserInfoModel();
        BeanUtil.copyNotNullProperties(po, infoModel);
        if (type.equals("idcard")) {
            if (authModel.getIdcardStatus() == 1){
                throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
            }
            if (StringUtils.isBlank(infoModel.getName()) || StringUtils.isBlank(infoModel.getIdcardNum())
                    || StringUtils.isBlank(infoModel.getIdcardAddress()) || StringUtils.isBlank(infoModel.getValidation())) {
                throw new BusinessException(UserRespCode.PARAM_IS_NULL);
            }
            String idCardNum = infoModel.getIdcardNum();
            //查询身份证号有没有被注册
            UserInfoModel userInfoModel = userInfoService.getUserInfoByIdCardNum(idCardNum, userModel.getAgencyId(), userModel.getVestId());
            if (userInfoModel != null && !userInfoModel.getUserId().equals(infoModel.getUserId())) {
                throw new BusinessException(UserRespCode.IDCARD_ALREADY_USE);
            }
            if (StringUtils.isNotBlank(idCardNum) && idCardNum.length() == 18) {
                userInfoService.save(infoModel, type, true);
            } else {
                throw new BusinessException(UserRespCode.AUTH_FAILED);
            }
        } else {
            if (authModel.getInfoStatus() == 1){
                throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
            }
            if (StringUtils.isBlank(infoModel.getEducation()) || StringUtils.isBlank(infoModel.getAddress())
                    || StringUtils.isBlank(infoModel.getAddressTime()) || StringUtils.isBlank(infoModel.getMarry())
                    || StringUtils.isBlank(infoModel.getJob()) || StringUtils.isBlank(infoModel.getJobName())
                    || StringUtils.isBlank(infoModel.getCompanyAddress()) || StringUtils.isBlank(infoModel.getFirstContact())
                    || StringUtils.isBlank(infoModel.getFirstPhone()) || StringUtils.isBlank(infoModel.getOtherContact()) || StringUtils.isBlank(infoModel.getOtherPhone())) {
                throw new BusinessException(UserRespCode.PARAM_IS_NULL);
            }
//            UserModel userModel = comUserService.selectUserModelById(model.getUserId());
            String firstPhone = PhoneUtil.trimSpaceAndAreaCode(infoModel.getFirstPhone());
            String otherPhone = PhoneUtil.trimSpaceAndAreaCode(infoModel.getOtherPhone());
            if (firstPhone.equals(userModel.getPhone()) || otherPhone.equals(userModel.getPhone())) {
                throw new BusinessException(UserRespCode.PHONE_HAS_EXIST);
            }
            infoModel.setFirstPhone(firstPhone);
            infoModel.setOtherPhone(otherPhone);
            infoModel.setUserId(userId);
            userInfoService.save(infoModel, type, true);
        }
        redisTemplate.opsForHash().delete(UserRedisKey.USER_DATA, infoModel.getUserId());
        return Response.success();
    }

    @GetMapping("/detail")
    public Response detail() {
        return Response.success(userInfoService.selectByUserId(UserUtil.getUserId()));
    }


    @GetMapping("/info")
    public Response info() {
        Integer userId = UserUtil.getUserId();
        if (userId == null) {
            return Response.success();
        }
        UserModel model = userDao.selectById(userId);
        if (model == null || model.getDeleted() == CommonConst.YES) {
	        return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyNotNullProperties(model, userVO);
        UserInfoModel userInfo = comUserService.findUserInfoByUserId(userId);
        if (userInfo != null) {
            userVO.setSex(userInfo.getSex());
        }
        return Response.success(userVO);
    }


    @PostMapping("/saveUserContact")
    public Response saveUserContact(String contactJson) {
        Assert.notNull(contactJson, "参数不能为空");
        log.info(">>>>saveUserContact param={}", contactJson);
        Integer userId = UserUtil.getUserId();
	    UserModel userModel = comUserService.selectUserModelById(userId);
	    if (userModel == null || userModel.getDeleted() == CommonConst.YES){
		    return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
	    }
        List<UserContactModel> list = JSON.parseArray(contactJson, UserContactModel.class);
        if (CollectionUtils.isEmpty(list)) return Response.success();
        userInfoService.saveUserContact(userId, list);

        userInfoService.doubtfulContactRelation(userId);

        return Response.success();
    }

    /**
     * 获取省市区列表
     *
     * @return
     */
    @GetMapping("/getAddressList")
    @LimitLess
    public Response getAddressList() {
        return Response.success(userInfoDao.getAddressList());
    }

}
