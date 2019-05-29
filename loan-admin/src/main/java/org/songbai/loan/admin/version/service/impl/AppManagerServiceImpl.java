package org.songbai.loan.admin.version.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.loan.admin.version.dao.AppManagerDao;
import org.songbai.loan.admin.version.model.vo.AppManagerVo;
import org.songbai.loan.admin.version.service.AppManagerService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.version.AppManagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppManagerServiceImpl implements AppManagerService {
    @Autowired
    AppManagerDao appManagerDao;

    @Override
    public Integer findAppManagerByAgencyId(Integer agencyId, Integer platform) {
        return appManagerDao.findAppManagerByAgencyId(agencyId, platform);
    }

    @Override
    public void addAppManager(AppManagerModel model) {
        AppManagerModel oldModel = appManagerDao.getInfoByAgencyId(model.getAgencyId(), model.getPlatform(), model.getVestId());
        if (oldModel != null) throw new BusinessException(AdminRespCode.REPEAT_CODE, "已存在，请勿重复提交");
        appManagerDao.insert(model);
    }

    @Override
    public void updateAppManager(AppManagerModel model) {
        AppManagerModel oldModel = appManagerDao.getInfoByAgencyId(model.getAgencyId(), model.getPlatform(), model.getVestId());
        if (oldModel != null && !oldModel.getId().equals(model.getId()))
            throw new ResolveMsgException("common.param.repeat", "已存在，请勿重复提交");
        appManagerDao.updateById(model);
    }

    @Override
    public List<AppManagerVo> queryAppManager(AppManagerModel model) {
        return appManagerDao.queryAppManager(model);
    }
}
