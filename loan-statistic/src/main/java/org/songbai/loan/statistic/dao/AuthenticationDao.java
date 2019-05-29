package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.statistic.UserStatisticModel;
import org.songbai.loan.model.user.AuthenticationModel;

public interface AuthenticationDao extends BaseMapper<AuthenticationModel>{


    UserStatisticModel findUserAuthenticationStatisticByAgencyIdAndToday(@Param("agencyId") Integer agencyId,  @Param("date") String date);

}
