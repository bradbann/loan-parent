package org.songbai.loan.service.user.service;


import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: qmw
 * Date: 2018/4/18 下午2:30
 */
public interface ComUserService {


    UserModel selectUserModelByThridId(String thridId);

    /**
     * 先去缓存查找，没有则取数据库
     */
    UserModel selectUserModelById(Integer userId);

    /**
     */
    UserInfoModel selectUserInfoByThridId(String thridId);

    /**
     * @param refresh 1和0  0则从数据库获取，并放入最新数据
     */
    UserModel selectUserModelById(Integer userId, int refresh);

    /**
     * 根据用户名查id
     */
    Set<Integer> selectUserIdByLikeUserName(String username);


    /**
     * 校验登录密码
     */
    boolean validateLoginPass(Integer userId, String password);

    /**
     * 查找代理下的用户
     */
    UserModel findUserModelByAgencyIdAndPhone(String phone, Integer vestId, Integer agencyId);

    /**
     * 查询用户的个人信息
     */
    UserInfoModel findUserInfoByUserId(Integer userId);

    /**
     * @param refresh 1和0  0则从数据库获取，并放入最新数据
     */
    UserInfoModel findUserInfoByUserId(Integer userId, Integer refresh);

    /**
     * 获取用户通讯录
     */
    List<UserContactModel> findUserContactListByUserId(Integer userId);

    /**
     * 根据手机号获取所有的用户
     */
    List<UserModel> findUserListByUserPhone(Integer agencyId, String phone);

    List<Map<String,String>> findChannelCodeList(Integer agencyId, Integer vestId);
}
