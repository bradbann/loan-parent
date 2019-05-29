package org.songbai.loan.user.appConfig.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.version.AppVersionModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.appConfig.model.vo.AppVestVo;
import org.songbai.loan.user.appConfig.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by hacfox on 09/10/2017
 */
@RestController
@RequestMapping("/appVersion")
@LimitLess
public class AppVersionController {
    @Autowired
    private AppVersionService appVersionService;
    @Autowired
    ComAgencyService comAgencyService;

    @RequestMapping(value = "/queryForceVersion", method = RequestMethod.GET)
    public Response queryForceVersion(HttpServletRequest request) {
        Integer platform = PlatformKit.parsePlatform(request).value;
//        Integer platform = 1;
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        AppVersionModel model = appVersionService.findByPlatform(platform,agencyId);
        if (model != null) {
            model.setModifyUser(null);
            model.setPlatform(null);
            model.setRemark(null);
        }
        return Response.success(model);
    }

}
