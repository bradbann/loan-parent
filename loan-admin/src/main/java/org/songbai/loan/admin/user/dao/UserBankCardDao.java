package org.songbai.loan.admin.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.user.UserBankCardModel;

import java.util.List;

public interface UserBankCardDao extends BaseMapper<UserBankCardModel> {

    void updateUserBankCardDeleted(Integer userId);


    //查询用户所有已绑定的银行卡
    List<UserBankCardModel> selectUserBindList(@Param("userId") Integer userId, @Param("status") Integer status);

    String getIconByBankCode(@Param("bankCode") String bankCode);
}
