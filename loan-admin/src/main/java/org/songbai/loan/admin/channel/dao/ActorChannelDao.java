package org.songbai.loan.admin.channel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.channel.ActorChannelModel;
import org.songbai.loan.model.channel.AgencyChannelModel;

import java.util.List;

public interface ActorChannelDao extends BaseMapper<ActorChannelModel> {

    List<AgencyChannelModel> findActorManagerList(@Param("agencyId") Integer agencyId, @Param("actorId") Integer actorId);

    void deleteByActorId(@Param("actorId") Integer actorId);

    void createActorChannel(@Param("list") List<ActorChannelModel> list);
}
