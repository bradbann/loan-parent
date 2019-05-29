package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.user.UserModel;

import java.util.List;
import java.util.Set;

public interface UserDao extends BaseMapper<UserModel>{



    List<UserModel> findUserStatis(@Param("page") PageRow pageRow);

    /**
     * 查找个推id不等于空的
     */
    Set<String> findUserIdGexingNotNull(@Param("ids") List<Integer> list);

    /**
     * 查询用户登录人数
     * @param agencyId
     * @return
     */
    Integer findUserLoginCountTodayByAgencyId(@Param("agencyId") Integer agencyId,  @Param("date") String date);

}
