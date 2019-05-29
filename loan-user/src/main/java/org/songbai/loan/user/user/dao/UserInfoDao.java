package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.user.user.model.vo.AreaVo;

import java.util.List;

public interface UserInfoDao extends BaseMapper<UserInfoModel> {

    UserInfoModel getUserInfoByIdCardNum(@Param("idCardNum") String idCardNum, @Param("agencyId") Integer agencyId,
                                         @Param("vestId") Integer vestId);

    List<AreaVo> getAddressList();
}
