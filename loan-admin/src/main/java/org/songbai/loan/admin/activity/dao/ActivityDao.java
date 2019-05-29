package org.songbai.loan.admin.activity.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.activity.model.vo.ActivityModelVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.activity.ActivityModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 11:50 AM
 */
public interface ActivityDao extends BaseMapper<ActivityModel> {
    int findActivityListByAgencyIdCount(@Param("status") Integer status, @Param("agencyId") Integer agencyId);

    List<ActivityModelVO> findActivityListByAgencyIdList(@Param("status") Integer status, @Param("agencyId") Integer agencyId, @Param("page") PageRow pageRow);
}
