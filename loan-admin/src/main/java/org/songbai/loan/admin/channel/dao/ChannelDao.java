package org.songbai.loan.admin.channel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.channel.model.po.ChannelQueryPo;
import org.songbai.loan.admin.channel.model.vo.AgencyChannelVo;
import org.songbai.loan.admin.channel.model.vo.ChannelUserVo;
import org.songbai.loan.model.channel.AgencyChannelModel;

import java.util.List;

public interface ChannelDao extends BaseMapper<AgencyChannelModel> {
    AgencyChannelModel getInfoByChannelCode(@Param("channleCode") String channelCode,
                                            @Param("channleStatus") Integer channelStatus,
                                            @Param("agencyId") Integer agencyId);

    Integer getChannelCount(@Param("model") AgencyChannelModel model);

    List<AgencyChannelModel> findChannelList(@Param("model") AgencyChannelModel model, @Param("limit") Integer limit,
                                             @Param("pageSize") Integer pageSize);

    List<AgencyChannelModel> findChannelCodeList(@Param("agencyId") Integer agencyId);

    Integer getMyCustomerCount(@Param("po") ChannelQueryPo po, @Param("channelIds") List<Integer> channelIds);

    List<ChannelUserVo> findMyCustomerList(@Param("po") ChannelQueryPo po, @Param("channelIds") List<Integer> channelIds);

    List<AgencyChannelVo> findChannelByAgencyId(@Param("agencyId") Integer agencyId);

    String findChannelNameByAgencyIdAndChannelCode(@Param("agencyId") Integer agencyId, @Param("channelCode") String channelCode);

}
