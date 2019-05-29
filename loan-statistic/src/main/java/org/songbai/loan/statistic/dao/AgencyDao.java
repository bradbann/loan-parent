package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.agency.AgencyModel;

import java.util.List;

public interface AgencyDao extends BaseMapper<AgencyModel> {


    List<Integer> findAgencyIdsNotContainsCloud();

}
