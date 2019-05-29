package org.songbai.loan.push.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.sms.model.SmsLog;

import java.util.List;
import java.util.Set;

public interface UserDao extends BaseMapper<UserModel> {


    Set<String> findDevicesByVestId(@Param("vestId") Integer vestId, @Param(value = "scopces") List<String> scopces);

    List<UserModel> findUserByPhoneAndAgencyId(@Param("phone") String phone, @Param("agencyId") Integer agencyId);

    UserModel findUserBySmsLos( SmsLog smsLog);

}
