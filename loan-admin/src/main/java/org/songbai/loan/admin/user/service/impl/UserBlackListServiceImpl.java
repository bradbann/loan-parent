package org.songbai.loan.admin.user.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.user.dao.*;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;
import org.songbai.loan.admin.user.service.UserBlackListService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.user.*;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户黑名单service
 *
 * @author wjl
 * @date 2018年10月30日 16:37:18
 * @description
 */
@Service
public class UserBlackListServiceImpl implements UserBlackListService {

    @Autowired
    private UserBlackListDao blackListDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private AuthenticationDao authDao;
    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private BlackListOneDao oneDao;
    @Autowired
    private BlackListTwoDao twoDao;
    @Autowired
    private UserBlackListReadyDao blackListReadyDao;
    @Autowired
    private ComAgencyService comAgencyService;

    @Override
    public Page<UserResultVo> getList(UserQueryVo model) {
        Integer total = blackListDao.getListCount(model);
        Page<UserResultVo> page = new Page<>(model.getPage(), model.getPageSize(), total);
        if (total > 0) {
            model.setLimit(model.getPage() * model.getPageSize());
            List<UserResultVo> list = blackListDao.getList(model);
            list.forEach(e -> {
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            });
            page.setData(list);
        } else {
            page.setData(new ArrayList<>());
        }
        return page;
    }

    @Override
    @Transactional
    public void addBlack(String thirdId, String name, String phone, String idcard, Integer status,
                         String limitStart, String limitEnd, String remark, AdminUserModel admin) {
        if (status == 1) {
            return;
        }
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return;
        }
        Integer agencyId = userModel.getAgencyId();
        if (admin.getDataId() != 0) {
            if (!agencyId.equals(admin.getDataId())) {
                return;
            }
        }
        Integer userId = userModel.getId();
        if (userModel.getStatus() == 1) {
            //然后加入黑名单列表  0 黑名单 2灰名单 3白名单
            Date start = SimpleDateFormatUtil.stringToDate(limitStart);
            Date end = SimpleDateFormatUtil.stringToDate(limitEnd);
            boolean flag = DateUtils.isSameDay(new Date(), start);
            if (status == CommonConst.NO) {
                UserBlackListModel model = new UserBlackListModel();
                model.setUserId(userId);
                model.setAgencyId(agencyId);
                model.setType(status);
                model.setBlackFrom("平台");
                model.setLimitStart(start);
                model.setLimitEnd(end);
                model.setRemark(remark);
                model.setOperator(admin.getName());
                blackListDao.insert(model);
                updateUserAndAuth(userId, status);
                addBlackList(name, idcard, phone);
                //删除redis中的user信息
                redis.opsForHash().delete(UserRedisKey.USER_INFO, userId);
            } else {
                if (flag) {
                    UserBlackListModel model = new UserBlackListModel();
                    model.setUserId(userId);
                    model.setAgencyId(agencyId);
                    model.setType(status);
                    model.setBlackFrom("平台");
                    model.setLimitStart(start);
                    model.setLimitEnd(end);
                    model.setRemark(remark);
                    model.setOperator(admin.getName());
                    blackListDao.insert(model);
                    updateUserAndAuth(userId, status);
                    addBlackListReady(userId, status, start, end, agencyId);
                } else {
                    addBlackListReady(userId, status, start, end, agencyId);
                }
            }
        }
    }


    @Override
    @Transactional
    public void removeBlack(String thirdId, String idcardNum, String phone, AdminUserModel admin) {
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return;
        }
        Integer userId = userModel.getId();
        UserBlackListModel blackListModel = blackListDao.selectById(userId);
        if (admin.getDataId() != 0) {
            if (!blackListModel.getAgencyId().equals(admin.getDataId())) {
                return;
            }
        }
        if (blackListModel != null) {//移除瞬时生效
            //删除黑名单
            blackListDao.deleteById(userId);
            updateUserAndAuth(userId, CommonConst.YES);
            if (blackListModel.getType() == CommonConst.NO) {//如实黑名单 需要删库
                deleteBlackList(idcardNum, phone);
            } else {
                UserBlackListReadyModel blackListReadyModel = blackListReadyDao.selectById(userId);
                if (blackListReadyModel != null) {
                    blackListReadyDao.deleteById(userId);
                }
            }
            redis.opsForHash().delete(UserRedisKey.USER_INFO, userId);
        }
    }


    @Override
    @Transactional
    public void updateBlack(String thirdId, String name, Integer type, String idcardNum,
                            String phone, String limitStart, String limitEnd, String remark, AdminUserModel admin) {
        if (type == 1) {
            return;
        }
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return;
        }
        Integer userId = userModel.getId();
        UserBlackListModel model = blackListDao.selectById(userId);
        if (admin.getDataId() != CommonConst.NO) {
            if (!model.getAgencyId().equals(admin.getDataId())) {
                return;
            }
        }
        Date start = SimpleDateFormatUtil.stringToDate(limitStart);
        Date end = SimpleDateFormatUtil.stringToDate(limitEnd);
        Date now = new Date();
        Integer oldType = model.getType();
        Integer agencyId = model.getAgencyId();

        //查询黑名单预备表
        UserBlackListReadyModel blackListReadyModel = blackListReadyDao.selectById(userId);

        if (DateUtils.isSameDay(start, now)) {//同一天立即生效
            model.setType(type);
            model.setLimitStart(start);
            model.setLimitEnd(end);
            model.setRemark(remark);
            blackListDao.updateById(model);
            updateUserAndAuth(model.getUserId(), type);
            if (!oldType.equals(type)) {//如果更改的不一样了
                if (oldType != CommonConst.NO) {//以前不是黑名单
                    if (type == CommonConst.NO) {//变为黑名单
                        addBlackList(name, idcardNum, phone);
                        redis.opsForHash().delete(UserRedisKey.USER_INFO, userId);
                        if (blackListReadyModel != null) {
                            blackListReadyDao.deleteById(userId);
                        }
                    } else {
                        blackListReadyModel.setStatus(type);
                        blackListReadyModel.setLimitEnd(end);
                        blackListReadyDao.updateById(blackListReadyModel);
                    }
                } else {//如果以前是黑名单 那么把哭给删除了
                    deleteBlackList(idcardNum, phone);
                }
            } else {
                model.setLimitStart(start);
                model.setLimitEnd(end);
                model.setRemark(remark);
                blackListDao.updateById(model);
                //更新预备表
                blackListReadyModel.setLimitEnd(end);
                blackListReadyDao.updateById(blackListReadyModel);
            }
        } else {//不是同一天
            if (!oldType.equals(type)) {//如果一样就不用更改user和auth
                if (oldType != CommonConst.NO) {//以前不是黑名单
                    if (type == CommonConst.NO) {//变为黑名单
                        model.setType(type);
                        model.setLimitStart(start);
                        model.setLimitEnd(end);
                        model.setRemark(remark);
                        blackListDao.updateById(model);
                        updateUserAndAuth(model.getUserId(), type);
                        addBlackList(name, idcardNum, phone);
                        redis.opsForHash().delete(UserRedisKey.USER_INFO + userId);
                        if (blackListReadyModel != null) {
                            blackListReadyDao.deleteById(userId);
                        }
                    } else {//灰变白
                        blackListReadyModel.setStatus(type);
                        blackListReadyModel.setLimitStart(start);
                        blackListReadyModel.setLimitEnd(end);
                        blackListReadyDao.updateById(blackListReadyModel);
                    }
                } else {//以前是黑
                    addBlackListReady(userId, type, start, end, agencyId);
                }
            } else {
                model.setLimitStart(start);
                model.setLimitEnd(end);
                model.setRemark(remark);
                blackListDao.updateById(model);
                addBlackListReady(userId, type, start, end, agencyId);
                blackListReadyModel.setLimitStart(start);
                blackListReadyModel.setLimitEnd(end);
                blackListReadyDao.updateById(blackListReadyModel);
            }
        }
        redis.opsForHash().delete(UserRedisKey.USER_INFO, userId);
    }

    private void addBlackListReady(Integer userId, Integer type, Date start, Date end, Integer agencyId) {
        UserBlackListReadyModel blackListReadyModel = new UserBlackListReadyModel();
        blackListReadyModel.setUserId(userId);
        blackListReadyModel.setAgencyId(agencyId);
        blackListReadyModel.setLimitStart(start);
        blackListReadyModel.setLimitEnd(end);
        blackListReadyModel.setStatus(type);
        blackListReadyDao.insert(blackListReadyModel);
    }


    @Transactional
    @Override
    public void updateUserAndAuth(Integer userId, Integer status) {
        //先更新user表中的状态
        UserModel user = new UserModel();
        user.setId(userId);
        user.setStatus(status);
        userDao.updateById(user);
        //然后更新认证表
        AuthenticationModel authModel = new AuthenticationModel();
        authModel.setUserId(userId);
        authModel.setType(status);
        authDao.updateById(authModel);
    }

    @Transactional
    @Override
    public void addBlackList(String name, String idcardNum, String phone) {
        //最后加入两张黑名单库
        BlackListOneModel one = new BlackListOneModel();
        one.setName(name);
        one.setIdcardNum(idcardNum);
        one.setBlackFrom("平台");
        oneDao.insert(one);
        BlackListTwoModel two = new BlackListTwoModel();
        two.setPhone(phone);
        two.setBlackFrom("平台");
        twoDao.insert(two);
    }

    @Transactional
    @Override
    public void deleteBlackList(String idcardNum, String phone) {
        //删除黑名单库中的东西
        Map<String, Object> map = new HashMap<>();
        map.put("idcard_num", idcardNum);
        oneDao.deleteByMap(map);
        map.clear();
        map.put("phone", phone);
        twoDao.deleteByMap(map);
    }
}
