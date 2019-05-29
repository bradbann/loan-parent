package org.songbai.loan.user.appConfig.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.appConfig.service.AppManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/appManager")
@LimitLess
public class AppManagerCtrl {
    @Autowired
    AppManagerService appManagerService;
    @Autowired
    ComAgencyService comAgencyService;


    @GetMapping(value = "/getAppManagerInfo")
    public Response getAppManagerInfo(HttpServletRequest request) {
        //Integer platform = PlatformKit.parsePlatform(request).value;
        //Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        Ret ret = Ret.create();
        ret.put("aboutUsUrl", "/aboutUs/aboutUs.html");
        return Response.success(ret);
    }

    @GetMapping(value = "/getAppConfigInfo")
    public Response getAppConfigInfo(String vestCode, Integer platform, HttpServletRequest request) {
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        AppVestModel vestModel = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vestModel == null) return Response.success();
        Integer vestId = vestModel.getId();

        return Response.success(appManagerService.getAppConfigInfo(vestId, agencyId, platform));
    }
}
