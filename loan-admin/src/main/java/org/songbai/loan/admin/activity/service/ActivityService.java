package org.songbai.loan.admin.activity.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.activity.model.vo.ActivityModelVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.activity.ActivityModel;

/**
 * Author: qmw
 * Date: 2018/12/17 11:52 AM
 */
public interface ActivityService {
    void addActivity(ActivityModel model);


    Page<ActivityModelVO> activityListByAgencyId(Integer status, Integer agencyId, PageRow pageRow);

    void deleteActivity(Integer id, Integer agencyId);

    void updateActivity(ActivityModel model);

    ActivityModelVO activityDetailByAgencyId(Integer id, Integer agencyId);

    void updateActivityModelStatus(Integer id, Integer status, Integer agencyId);

    void pushMsg(Integer id, Integer agencyId);

}
