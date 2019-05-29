package org.songbai.loan.admin.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.model.UserResultVo;
import org.songbai.loan.model.user.UserBlackListModel;

import java.util.List;

public interface UserBlackListDao extends BaseMapper<UserBlackListModel>{
	
	public Integer getListCount(UserQueryVo model);

	public List<UserResultVo> getList(UserQueryVo model);

	public List<UserBlackListModel> getListByType();

}
