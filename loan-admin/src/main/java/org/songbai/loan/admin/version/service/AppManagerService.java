package org.songbai.loan.admin.version.service;

import org.songbai.loan.admin.version.model.vo.AppManagerVo;
import org.songbai.loan.model.version.AppManagerModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AppManagerService {
    Integer findAppManagerByAgencyId(Integer agencyId, Integer platform);

    void addAppManager(AppManagerModel model);

    void updateAppManager(AppManagerModel model);

    List<AppManagerVo> queryAppManager(AppManagerModel model);
}
