package org.songbai.loan.admin.channel.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.channel.dao.ActorChannelDao;
import org.songbai.loan.admin.channel.service.ActorChannelService;
import org.songbai.loan.model.channel.ActorChannelModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorChannelServiceImpl implements ActorChannelService {
    @Autowired
    ActorChannelDao actorChannelDao;

    @Override
    public List<AgencyChannelModel> findActorManagerList(Integer agencyId, Integer actorId) {
        return actorChannelDao.findActorManagerList(agencyId, actorId);
    }

    @Override
    @Transactional
    public void grantActorChannel(Integer agencyId, Integer actorId, String channelIds) {

        actorChannelDao.deleteByActorId(actorId);

        List<Integer> ids = Arrays.asList(StringUtil.split2Int(channelIds));

        if (CollectionUtils.isNotEmpty(ids)) {
            List<ActorChannelModel> list = ids.stream().map(channelId -> {
                ActorChannelModel model = new ActorChannelModel();
                model.setAgencyId(agencyId);
                model.setActorId(actorId);
                model.setChannelId(channelId);
                return model;
            }).collect(Collectors.toList());
            actorChannelDao.createActorChannel(list);
        }


    }

    @Override
    public void deleteByActorId(Integer actorId) {
        actorChannelDao.deleteByActorId(actorId);
    }
}
