package org.songbai.loan.user.activity.service;

import org.songbai.loan.model.activity.ActivityModel;

/**
 * Author: qmw
 * Date: 2018/12/17 11:52 AM
 */
public interface ActivityService {

	ActivityModel findPopupActivityByAgencyId(Integer agencyId);

    ActivityModel findPopupActivityByAgencyId(Integer agencyId, Integer platform, Integer vestId);
}
