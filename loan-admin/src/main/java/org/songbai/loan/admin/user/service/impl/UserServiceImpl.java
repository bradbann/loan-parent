package org.songbai.loan.admin.user.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.channel.dao.ChannelDao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.songbai.loan.admin.user.dao.AuthenticationDao;
import org.songbai.loan.admin.user.dao.UserBankCardDao;
import org.songbai.loan.admin.user.dao.UserDao;
import org.songbai.loan.admin.user.dao.UserInfoDao;
import org.songbai.loan.admin.user.model.UserInfoVo;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;
import org.songbai.loan.admin.user.service.UserService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.*;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台用户srevice
 *
 * @author wjl
 * @date 2018年10月30日 10:40:16
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private AuthenticationDao authDao;
    @Autowired
    private UserBankCardDao bankCardDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SpringProperties springProperties;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    OrderDao orderDao;
    @Autowired
    private UserInfoDao userInfoDao;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Page<UserResultVo> userList(UserQueryVo model) {
        Integer count = userDao.findUserCount(model);
        Page<UserResultVo> page = new Page<>(model.getPage(), model.getPageSize(), count);
        if (count > 0) {
            model.setLimit(model.getPage() * model.getPageSize());
            List<UserResultVo> list = userDao.findUserList(model);
            list.forEach(e -> {
                if (e.getRemainDays() != null && e.getRemainDays() < 0) {
                    e.setRemainDays(null);
                }
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
                if (e.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                    if (vestModel != null) e.setVestName(vestModel.getName());
                }
                if (StringUtil.isNotEmpty(e.getChannelCode())) {
                    AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(e.getAgencyId(), e.getChannelCode());
                    if (channel != null && StringUtil.isNotEmpty(channel.getChannelName())) {
                        e.setChannelCode(channel.getChannelName());
                    }
                }
            });
            page.setData(list);
        } else {
            page.setData(new ArrayList<>());
        }
        return page;
    }

    @Override
    public Ret userDetail(String thirdId, Integer agencyId) {
        //先查询用户信息
        UserModel user = comUserService.selectUserModelByThridId(thirdId);
        if (user == null) return null;

        if (agencyId != 0 && !agencyId.equals(user.getAgencyId())) return null;

        UserResultVo vo = new UserResultVo();
        BeanUtil.copyNotNullProperties(user, vo);
        AgencyModel agencyModel = comAgencyService.findAgencyById(user.getAgencyId());
        if (agencyModel != null) vo.setAgencyName(agencyModel.getAgencyName());
        if (vo.getVestId() != null) {
            AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
            if (vestModel != null) vo.setVestName(vestModel.getName());
        }

        if (user.getChannelId() != null) {
            AgencyChannelModel model = channelDao.selectById(user.getChannelId());
            if (model != null) vo.setChannelName(model.getChannelName());
        }

        Integer userId = user.getId();
        //查询用户认证信息
        AuthenticationModel authModel = authDao.selectById(userId);
        //查询用户身份证信息以及个人信息
        UserInfoModel infoModel = comUserService.findUserInfoByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        if (infoModel != null) {
            if (StringUtils.isNotBlank(infoModel.getIdcardFrontImg()))
                infoModel.setIdcardFrontImg("/admin/user/img.do?url=" + infoModel.getIdcardFrontImg());
            if (StringUtils.isNotBlank(infoModel.getIdcardBackImg()))
                infoModel.setIdcardBackImg("/admin/user/img.do?url=" + infoModel.getIdcardBackImg());
            if (StringUtils.isNotBlank(infoModel.getLivingImg()))
                infoModel.setLivingImg("/admin/user/img.do?url=" + infoModel.getLivingImg());
            BeanUtil.copyNotNullProperties(infoModel, userInfoVo);

            if (StringUtils.isNotEmpty(infoModel.getFirstPhone())) {
                Query contractQuery = new Query();
                contractQuery.addCriteria(Criteria.where("userId").is(userId));
                contractQuery.addCriteria(Criteria.where("phone").is(infoModel.getFirstPhone()));
                UserContactModel contactModel = mongoTemplate.findOne(contractQuery, UserContactModel.class);
                if (contactModel != null && StringUtils.isNotBlank(contactModel.getName()))
                    userInfoVo.setFirstName(contactModel.getName());
            }

            if (StringUtils.isNotBlank(infoModel.getOtherPhone())) {
                Query contractQuery = new Query();
                contractQuery.addCriteria(Criteria.where("userId").is(userId));
                contractQuery.addCriteria(Criteria.where("phone").is(infoModel.getOtherPhone()));
                UserContactModel contactModel = mongoTemplate.findOne(contractQuery, UserContactModel.class);
                if (contactModel != null && StringUtils.isNotBlank(contactModel.getName()))
                    userInfoVo.setOtherName(contactModel.getName());
            }
        }


        //查询用户绑卡信息
        List<UserBankCardModel> bankList = bankCardDao.selectUserBindList(userId, FinanceConstant.BankCardStatus.BIND.key);
        //查询用户通讯录
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<UserContactModel> userContactModels = mongoTemplate.find(query, UserContactModel.class);
        Ret ret = Ret.create();

        //公债记录
        List<UserModel> userList = comUserService.findUserListByUserPhone(null, user.getPhone());
        if (CollectionUtils.isNotEmpty(userList)) {
            List<Integer> userIds = userList.stream().map(UserModel::getId).collect(Collectors.toList());
            Map platformMap = orderDao.queryCommonDebt(null, userIds);
            if (platformMap != null) ret.put("platFormDept", platformMap);
            Map agencyMap = orderDao.queryCommonDebt(user.getAgencyId(), userIds);
            if (agencyMap != null) ret.put("agencyDept", agencyMap);
        }

        ret.put("user", vo);
        ret.put("auth", authModel);
        ret.put("info", userInfoVo);
        ret.put("bankcard", bankList);
        ret.put("contact", userContactModels);

        return ret;
    }

    @Override
    public void deAuth(String thirdId, Integer agencyId) {
        //先查询用户信息
        UserModel user = comUserService.selectUserModelByThridId(thirdId);
        if (user == null) {
            throw new BusinessException(AdminRespCode.USER_NOT_EXISIT);
        }
        if (agencyId != 0 && !agencyId.equals(user.getAgencyId())) {
            throw new BusinessException(AdminRespCode.ACCESS_NOT_BELONG_AGENCY);
        }

        AuthenticationModel authenticationModel = new AuthenticationModel();
        authenticationModel.setUserId(user.getId());
        AuthenticationModel authModel = authDao.selectOne(authenticationModel);
        authenticationModel.setPhoneStatus(0);
        authenticationModel.setAlipayStatus(0);
        authenticationModel.setStatus(0);
        authenticationModel.setMoney(authModel.getMoney() - springProperties.getInteger("user.auth.phone", 100) - springProperties.getInteger("user.auth.alipay", 80));
        authDao.updateById(authenticationModel);

    }

    @Override
    @Transactional
    public void updateUserIdCardInfo(UserInfoModel userInfo, String thirdId, Integer agencyId) {
        UserInfoModel oldModel = comUserService.selectUserInfoByThridId(thirdId);
        if (oldModel == null) {
            throw new BusinessException(AdminRespCode.USER_NOT_EXISIT);
        }

        userInfo.setUserId(oldModel.getUserId());
        userDao.updateUserIdCardInfo(userInfo);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_DATA, oldModel.getUserId());
        if (!userInfo.getName().equals(oldModel.getName())) {
            UserModel userModel = new UserModel();
            userModel.setId(oldModel.getUserId());
            userModel.setName(userInfo.getName());
            userDao.updateById(userModel);

            redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, oldModel.getUserId());
        }
    }

    @Override
    public List<OrderPageVo> findUserOrderHistList(String thirdId, Integer agencyId) {
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        List<UserModel> userList = comUserService.findUserListByUserPhone(agencyId, userModel.getPhone());
        if (CollectionUtils.isNotEmpty(userList)) {
            List<Integer> userIds = userList.stream().map(UserModel::getId).collect(Collectors.toList());
            List<OrderPageVo> list = orderDao.findUserOrderHistList(agencyId, userIds);
            list.forEach(e -> {
                if (e.getAgencyId() != null) {
                    AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
                }
                if (e.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                    if (vestModel != null) e.setVestName(vestModel.getName());
                }
                String statusName = OrderConstant.handleOrderStatus(e.getStage(), e.getStatus());
                e.setOrderStatusName(statusName);
            });
            return list;
        }
        return null;
    }

    @Override
    @Transactional
    public void logOffUser(String thirdId, Integer agencyId) {
        UserModel u = userDao.findUserByUserThirdIdAndAgencyId(thirdId, agencyId);
        if (u == null || u.getDeleted() == CommonConst.DELETED_YES) {
            return;
        }
        OrderModel dbOrderModel = orderDao.finRecentOrderByUserId(u.getId());
        if (dbOrderModel != null) {
            Integer stage = dbOrderModel.getStage();
            Integer status = dbOrderModel.getStatus();
            if (stage == OrderConstant.Stage.ARTIFICIAL_AUTH.key) {
                if (status != OrderConstant.Status.FAIL.key) throw new BusinessException(AdminRespCode.ORDER_NOT_COMPLATE);
            }
            if (stage == OrderConstant.Stage.LOAN.key) {
                if (status != OrderConstant.Status.FAIL.key) throw new BusinessException(AdminRespCode.ORDER_NOT_COMPLATE);
            }
            if (stage == OrderConstant.Stage.REPAYMENT.key) {
                if (status != OrderConstant.Status.SUCCESS.key
                        || status != OrderConstant.Status.OVERDUE_LOAN.key
                        || status != OrderConstant.Status.ADVANCE_LOAN.key
                        || status != OrderConstant.Status.CHASE_LOAN.key) throw new BusinessException(AdminRespCode.ORDER_NOT_COMPLATE);
            }
        }

        UserModel update = new UserModel();
        update.setId(u.getId());
        update.setDeleted(CommonConst.DELETED_YES);
        userDao.updateById(update);
        // 清除用户表缓存
        redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, u.getId());


        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUserId(u.getId());
        userInfoModel.setDeleted(CommonConst.DELETED_YES);
        userInfoDao.updateById(userInfoModel);
        // 清除用户认证表缓存
        redisTemplate.opsForHash().delete(UserRedisKey.USER_DATA, u.getId());

        bankCardDao.updateUserBankCardDeleted(u.getId());

        String errorTimesKey = UserRedisKey.USER_LOGIN_ERROR_TIMES + ":" + u.getVestCode() + ":" + u.getPhone();
        //清除用户登录错误次数
        redisTemplate.delete(errorTimesKey);

        //清除用户登录信息缓存
        String oldToken = (String) redisTemplate.opsForHash().get("_session_user_bind", u.getId());
        redisTemplate.delete("_session_user:" + oldToken);
        redisTemplate.opsForHash().delete("_session_user_bind", u.getId());

        logger.info("用户注销,userId={}", u.getId());
    }

}
