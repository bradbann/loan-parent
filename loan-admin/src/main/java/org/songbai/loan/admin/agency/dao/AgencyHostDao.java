package org.songbai.loan.admin.agency.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.agency.AgencyHostModel;

import java.util.List;

public interface AgencyHostDao extends BaseMapper<AgencyHostModel> {
    void deleteByAgencyId(@Param("agencyId") Integer agencyId);

    List<AgencyHostModel> findHostListByAgencyId(@Param("agencyId") Integer agencyId);
}
