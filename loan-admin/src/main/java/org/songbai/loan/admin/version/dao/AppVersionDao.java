package org.songbai.loan.admin.version.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.version.model.vo.AppVersionVO;
import org.songbai.loan.model.version.AppVersionModel;

import java.util.List;

/**
 * Created by hacfox on 09/10/2017
 */
public interface AppVersionDao extends BaseMapper<AppVersionModel> {
    Integer getCount(@Param("model") AppVersionVO model);

    Integer findVersionByAgencyId(@Param("agencyId") Integer agencyId, @Param("platform") Integer platform,
                                  @Param("vestId") Integer vestId);

    AppVersionModel findInfoByAgencyIdAndPlatform(@Param("agencyId") Integer agencyId, @Param("platform") Integer platform,
                                                  @Param("vestId") Integer vestId);

    List<AppVersionVO> findVersionPage(@Param("model") AppVersionVO model, @Param("limit") Integer limit,
                                       @Param("pageSize") Integer pageSize);
}
