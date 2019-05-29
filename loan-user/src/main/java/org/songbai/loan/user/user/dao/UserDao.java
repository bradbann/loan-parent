package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.user.UserModel;

public interface UserDao extends BaseMapper<UserModel> {


	UserModel findBlackList(Integer agencyId, String phone);

    void updateOtherUserGexing(@Param("gexing") String gexing, @Param("id") Integer id);

}
