package org.songbai.loan.user.appConfig.service.impl;

import org.songbai.loan.model.version.AppVersionModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.user.appConfig.dao.AppVersionDao;
import org.songbai.loan.user.appConfig.dao.AppVestDao;
import org.songbai.loan.user.appConfig.model.vo.AppVestVo;
import org.songbai.loan.user.appConfig.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hacfox on 09/10/2017
 */
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionDao appVersionDao;

    @Autowired
    private AppVestDao appVestDao;

    @Override
    public AppVersionModel findByPlatform(Integer platform, Integer agencyId) {
        return appVersionDao.findByPlatform(platform,agencyId);
    }

}