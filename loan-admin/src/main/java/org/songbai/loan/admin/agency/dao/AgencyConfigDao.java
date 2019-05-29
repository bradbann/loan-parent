package org.songbai.loan.admin.agency.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.agency.AgencyConfigModel;

import java.util.List;

public interface AgencyConfigDao extends BaseMapper<AgencyConfigModel> {
    Integer findAgencyConfigCount(@Param("model") AgencyConfigModel model, @Param("agencyId") Integer agencyId);

    List<AgencyConfigModel> findAgencyConfigPage(@Param("model") AgencyConfigModel model,
                                                 @Param("agencyId") Integer agencyId,
                                                 @Param("limit") Integer limit,@Param("pageSize") Integer pageSize);
}
