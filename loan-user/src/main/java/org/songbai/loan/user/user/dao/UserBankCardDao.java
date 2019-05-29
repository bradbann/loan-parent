package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.user.user.model.vo.UserBankCardVo;

import java.util.List;

public interface UserBankCardDao extends BaseMapper<UserBankCardModel> {

	/**
	 * 查找用户所有在用的银行卡列表
	 */
	List<UserBankCardModel> selectUserBankListByUserIdStatus(@Param("userId") Integer userId, @Param("status") Integer status);

	UserBankCardModel getBankCardByCardNum(@Param("bankCardNum") String bankCardNum, @Param("status") Integer status, @Param("agencyId") Integer agencyId);

	//查询用户默认绑的卡
	UserBankCardModel getUserBindCard(@Param("userId") Integer userId, @Param("type") Integer type, @Param("status") Integer status);

	//查询用户所有已绑定的银行卡
	List<UserBankCardModel> selectUserBindList(@Param("userId") Integer userId, @Param("status") Integer status);

	//查询用户绑的卡以及支付宝微信
	List<UserBankCardVo> selectAllList(@Param("userId") Integer userId, @Param("status") Integer status);

	//根据code查询icon
	String getIconByBankCode(@Param("bankCode") String bankCode);

	UserBankCardModel getBankModelByPlatformIdAndBankCode(@Param("platformId") Integer platformId, @Param("bankCode") String bankCode);

	void updateBankCardModelById(UserBankCardModel model);
}
