package org.songbai.loan.user.appConfig.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.user.appConfig.model.vo.AppVestVo;

/**
 * Created by hacfox on 09/10/2017
 */
public interface AppVestDao {
    public AppVestModel findByVersionAndPlatform(@Param("vo") AppVestVo vo);
}
