package org.songbai.loan.admin.version.service.impl;

import org.songbai.loan.admin.version.dao.AppVersionDao;
import org.songbai.loan.admin.version.model.vo.AppVersionVO;
import org.songbai.loan.admin.version.service.AppVersionService;
import org.songbai.loan.model.version.AppVersionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hacfox on 09/10/2017
 */
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionDao appVersionDao;


    @Override
    public Integer update(AppVersionModel model) {
        return appVersionDao.updateById(model);
    }

    @Override
    public Integer getCount(AppVersionVO model) {
        return appVersionDao.getCount(model);
    }

    @Override
    public Integer addVersion(AppVersionModel model) {
        return appVersionDao.insert(model);
    }


    @Override
    public Integer findVersionByAgencyId(Integer agencyId, Integer platform, Integer vestId) {
        return appVersionDao.findVersionByAgencyId(agencyId, platform, vestId);
    }

    @Override
    public AppVersionModel findInfoByAgencyIdAndPlatform(Integer agencyId, Integer platform, Integer vestId) {
        return appVersionDao.findInfoByAgencyIdAndPlatform(agencyId, platform, vestId);
    }

    @Override
    public List<AppVersionVO> findVersionPage(AppVersionVO model, Integer limit, Integer pageSize) {
        return appVersionDao.findVersionPage(model, limit, pageSize);
    }
}
