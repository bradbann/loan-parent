package org.songbai.loan.service.user.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;

import java.util.List;
import java.util.Set;

/**
 * Author: qmw
 * Date: 2018/4/18 下午2:31
 */
public interface ComUserDao {
    /**
     * 根据id查询用户
     */
    UserModel selectUserModelById(Integer userId);

    UserModel selectUserModelByThridId(@Param("thirdId") String thirdId);

    Set<Integer> selectUserIdByLikeUserName(@Param("username") String username);

    Set<Integer> selectUserIdByAgencyIdOrPromoterId(@Param("agencyType") int agencyType, @Param("id") Integer id);

    /**
     * 根据id查找用户类型
     *
     * @param userId
     * @return
     */
    Integer getUserTypeByUserId(@Param("userId") Integer userId);


    /**
     * 根据用户id查询api权限
     *
     * @param userId
     * @return
     */
    Integer getPromoterByUserId(@Param("userId") Integer userId);


    /**
     * 根据用户id查询api权限
     *
     * @param id
     * @return
     */
    Integer getPromoterLevelAPiById(@Param("id") Integer id);

    /**
     * 根据用户id查询代理权限
     */
    Integer getAgencyByUserId(@Param("agencyId") Integer agencyId);

    UserModel findUserInfo(@Param("user") UserModel user);

    UserModel findUserModelByAgencyIdAndPhone(@Param("phone") String phone, @Param("vestId") Integer vestId, @Param("agencyId") Integer agencyId);


    /**
     * 查询用户的个人信息
     */
    UserInfoModel findUserInfoByUserId(Integer userId);

    List<UserModel> findUserListByUserPhone(@Param("agencyId") Integer agencyId, @Param("phone") String phone);

    List<String> findChannelCodeList(@Param("agencyId") Integer agencyId, @Param("vestId") Integer vestId);

    AgencyChannelModel findChannelNameByChannelCode(@Param("channelCode") String channelCode);

}
