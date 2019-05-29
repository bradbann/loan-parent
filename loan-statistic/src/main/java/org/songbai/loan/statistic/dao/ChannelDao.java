package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.channel.AgencyChannelModel;

public interface ChannelDao extends BaseMapper<AgencyChannelModel> {
    AgencyChannelModel getInfoByIdAndAgencyId(@Param("channelId") Integer channelId, @Param("agencyId") Integer agencyId);
}
