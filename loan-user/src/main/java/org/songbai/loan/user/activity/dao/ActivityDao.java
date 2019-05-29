package org.songbai.loan.user.activity.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.activity.ActivityModel;

/**
 * Author: qmw
 * Date: 2018/12/17 11:50 AM
 */
public interface ActivityDao extends BaseMapper<ActivityModel> {
    ActivityModel findPopupActivityByAgencyId(@Param("agencyId") Integer agencyId);

    ActivityModel findPopupActivity(@Param("agencyId") Integer agencyId, @Param("platformStr") String platformStr, @Param("vestIdStr") String vestIdStr);

}
