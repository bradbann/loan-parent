package org.songbai.loan.admin.order.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.finance.FinancePlatformConfigModel;
import org.songbai.loan.model.finance.FinancePlatformModel;
import org.songbai.loan.model.finance.PlatformConfig;

import java.util.List;

/**
 * @author: wjl
 * @date: 2018/12/20 15:48
 * Description:
 */
public interface FinancePlatformConfigDao extends BaseMapper<FinancePlatformConfigModel> {

	List<FinancePlatformModel> selectPlatformList();

	List<PlatformConfig> selectPlatformConfigListByAgencyId(@Param("agencyId") Integer agencyId, @Param("status") Integer status, @Param("platformCode") String platformCode);
}
