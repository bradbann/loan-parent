package org.songbai.loan.admin.user.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;
import org.songbai.loan.model.user.UserInfoModel;

import java.util.List;

public interface UserService {
	
	Page<UserResultVo> userList(UserQueryVo model);
	
	Ret userDetail(String thirdId, Integer agencyId);

	void deAuth(String thirdId, Integer agencyId);

    void updateUserIdCardInfo(UserInfoModel userInfo, String thirdId, Integer agencyId);

    List<OrderPageVo> findUserOrderHistList(String thirdId, Integer agencyId);

    void logOffUser(String userId, Integer agencyId);

}
