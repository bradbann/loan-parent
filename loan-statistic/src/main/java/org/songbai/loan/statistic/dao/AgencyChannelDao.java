package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.statistic.ChannelStatisModel;

import java.util.List;
import java.util.Map;

public interface AgencyChannelDao extends BaseMapper<AgencyChannelModel> {
    List<AgencyChannelModel> fingChannleList();

    List<Map<String, Object>> findUserCountGroup(@Param("createTime") String createTime);

    List<Map<String,Object>> findOrderCountGroup(@Param("createTime") String createTime);

    void insertStatisList(@Param("list") List<ChannelStatisModel> list);
}
