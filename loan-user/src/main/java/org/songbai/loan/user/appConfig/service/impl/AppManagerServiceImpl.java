package org.songbai.loan.user.appConfig.service.impl;

import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppManagerModel;
import org.songbai.loan.user.appConfig.dao.AppManagerDao;
import org.songbai.loan.user.appConfig.service.AppManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppManagerServiceImpl implements AppManagerService {
    @Autowired
    AppManagerDao appManagerDao;


    @Override
    public AppManagerModel getAppManagerInfo(Integer platform, Integer agencyId) {
        return appManagerDao.getAppManagerInfo(platform, agencyId);
    }

    @Override
    public AppManagerModel getAppConfigInfo(Integer vestId, Integer agencyId, Integer platform) {
        return appManagerDao.getAppConfigInfo(agencyId, vestId, platform);
    }
}
