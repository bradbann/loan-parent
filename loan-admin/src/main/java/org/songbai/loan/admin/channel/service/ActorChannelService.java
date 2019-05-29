package org.songbai.loan.admin.channel.service;

import org.songbai.loan.model.channel.AgencyChannelModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ActorChannelService {
    List<AgencyChannelModel> findActorManagerList(Integer agencyId, Integer actorId);

    void grantActorChannel(Integer agencyId, Integer actorId, String channelIds);

    void deleteByActorId(Integer actorId);

}
