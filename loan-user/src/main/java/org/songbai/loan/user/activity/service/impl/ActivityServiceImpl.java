package org.songbai.loan.user.activity.service.impl;

import org.songbai.loan.model.activity.ActivityModel;
import org.songbai.loan.user.activity.dao.ActivityDao;
import org.songbai.loan.user.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: qmw
 * Date: 2018/12/17 1:32 PM
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;


    @Override
    public ActivityModel findPopupActivityByAgencyId(Integer agencyId) {
        return activityDao.findPopupActivityByAgencyId(agencyId);
    }

    @Override
    public ActivityModel findPopupActivityByAgencyId(Integer agencyId, Integer platform, Integer vestId) {

        String platformStr = "," + platform + ",";
        String vestIdStr = "," + vestId + ",";

        return activityDao.findPopupActivity(agencyId, platformStr, vestIdStr);
    }
}
