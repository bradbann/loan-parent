package org.songbai.loan.admin.statistic.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.vo.UserStatisVo;

import java.util.List;

public interface StatisticDao {

    Integer queryUserStatisCount(@Param("po") ChannelStatisPo po);

    List<UserStatisVo> findUserStatisList(@Param("po") ChannelStatisPo po);
}
