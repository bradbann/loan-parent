package org.songbai.loan.admin.user.service;


import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;

public interface UserBlackListService {
	
	Page<UserResultVo> getList(UserQueryVo model);

	void addBlack(String thirdId,String name,String phone,String idcard,Integer status,
			String limitStart,String limitEnd,String remark, AdminUserModel admin);
	
	void removeBlack(String thirdId,String idcardNum,String phone, AdminUserModel admin);
	
	void updateBlack(String thirdId,String name,Integer type,String idcardNum,String phone,String limitStart,String limitEnd,String remark, AdminUserModel admin);

	void updateUserAndAuth(Integer userId, Integer status);

	void addBlackList(String name, String idcardNum, String phone);

	void deleteBlackList(String idcardNum, String phone);
}
