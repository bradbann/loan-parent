package org.songbai.loan.sms.dao;

import org.apache.ibatis.annotations.Param;

public interface LiveUserDao {
	public String findUserName(@Param("role") Integer role, @Param("userId") Integer userId);

	public Integer findUserTopChannelId(@Param("userId") Integer userId);

	public Integer findUserTopChannelIdByTeacherId(@Param("teacherId") Integer teacherId);
}
