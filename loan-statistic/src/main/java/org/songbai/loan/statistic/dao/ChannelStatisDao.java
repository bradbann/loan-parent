package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.statistic.ChannelStatisModel;

import java.time.LocalDate;

public interface ChannelStatisDao extends BaseMapper<ChannelStatisModel> {
    ChannelStatisModel getInfoByChannelId(@Param("agencyId") Integer agencyId, @Param("channelId") Integer channelId,
                                          @Param("calcDate") LocalDate calcDate);

    Integer updateChannelStatis(@Param("model") ChannelStatisModel model);
}
