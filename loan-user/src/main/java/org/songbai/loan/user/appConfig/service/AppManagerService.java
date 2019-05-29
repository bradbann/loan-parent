package org.songbai.loan.user.appConfig.service;

import org.songbai.loan.model.version.AppManagerModel;
import org.springframework.stereotype.Component;

@Component
public interface AppManagerService {
    AppManagerModel getAppManagerInfo(Integer platform, Integer agencyId);

    AppManagerModel getAppConfigInfo(Integer vestId, Integer agencyId, Integer platform);
}
