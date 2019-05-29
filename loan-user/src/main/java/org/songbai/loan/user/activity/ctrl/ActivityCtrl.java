package org.songbai.loan.user.activity.ctrl;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: qmw
 * Date: 2018/12/17 3:12 PM
 */
@RestController
@RequestMapping("/activity")
public class ActivityCtrl {
	@Autowired
	private ActivityService activityService;
	@Autowired
	private ComAgencyService comAgencyService;

	@LimitLess
	@GetMapping("/popup")
	public Response popup(HttpServletRequest request, Integer platform) {
		Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

		String vestCode = PlatformKit.parseChannel(request);
		Integer vestId = comAgencyService.findVestIdByVestCode(agencyId,vestCode);

		if (platform == null) {
			platform = PlatformKit.parsePlatform(request).value;
		}



		return Response.success(activityService.findPopupActivityByAgencyId(agencyId,platform,vestId));
	}

}
