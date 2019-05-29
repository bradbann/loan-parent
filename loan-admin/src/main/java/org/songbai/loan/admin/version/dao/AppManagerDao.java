package org.songbai.loan.admin.version.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.version.model.vo.AppManagerVo;
import org.songbai.loan.model.version.AppManagerModel;

import java.util.List;

public interface AppManagerDao extends BaseMapper<AppManagerModel> {
    Integer findAppManagerByAgencyId(@Param("agencyId") Integer agencyId, @Param("platform") Integer platform);

    AppManagerModel getInfoByAgencyId(@Param("agencyId") Integer agencyId, @Param("platform") Integer platform,
                                      @Param("vestId") Integer vestId);

    List<AppManagerVo> queryAppManager(@Param("model") AppManagerModel model);
}
