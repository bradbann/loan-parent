package org.songbai.loan.admin.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;

import java.util.List;

public interface UserDao extends BaseMapper<UserModel>{

	 List<UserResultVo> findUserList(@Param("model") UserQueryVo model);
	
	 Integer findUserCount(@Param("model")UserQueryVo model);

	 List<Integer> getUserIdByIdcardNum(String idcardNum);

    void updateUserIdCardInfo(@Param("userInfo") UserInfoModel userInfo);

    UserModel findUserByUserThirdIdAndAgencyId(@Param("thirdId") String thirdId, @Param("agencyId") Integer agencyId);

}
