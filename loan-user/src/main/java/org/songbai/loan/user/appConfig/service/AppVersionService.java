package org.songbai.loan.user.appConfig.service;


import org.songbai.loan.model.version.AppVersionModel;
import org.songbai.loan.user.appConfig.model.vo.AppVestVo;

/**
 * Created by hacfox on 09/10/2017
 */
public interface AppVersionService {

    AppVersionModel findByPlatform(Integer platform, Integer agencyId);

}
