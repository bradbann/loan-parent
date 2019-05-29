package org.songbai.loan.user.appConfig.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.version.AppVersionModel;

/**
 * Created by hacfox on 09/10/2017
 */
public interface AppVersionDao {
    AppVersionModel findByPlatform(@Param("platform") Integer platform, @Param("agencyId") Integer agencyId);
}
