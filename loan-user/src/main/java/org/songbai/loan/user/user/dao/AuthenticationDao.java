package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.user.AuthenticationModel;

public interface AuthenticationDao extends BaseMapper<AuthenticationModel> {

	AuthenticationModel findUserAuthenticationByUserId(Integer userId);

	void updateAuthStatusAndAtomicMoneyById(AuthenticationModel update);

}
