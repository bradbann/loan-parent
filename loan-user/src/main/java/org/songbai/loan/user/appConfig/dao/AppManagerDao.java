package org.songbai.loan.user.appConfig.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.version.AppManagerModel;


public interface AppManagerDao extends BaseMapper<AppManagerModel> {
	AppManagerModel getAppManagerInfo(@Param("platform") Integer platform, @Param("agencyId") Integer agencyId);

    AppManagerModel getAppConfigInfo(@Param("agencyId") Integer agencyId, @Param("vestId") Integer vestId,
                                     @Param("platform") Integer platform);
}
