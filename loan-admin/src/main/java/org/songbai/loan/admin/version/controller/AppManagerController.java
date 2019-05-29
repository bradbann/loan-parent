package org.songbai.loan.admin.version.controller;


import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.version.service.AppManagerService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.version.AppManagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * app关于我们
 */
@RestController
@RequestMapping("/appManager")
public class AppManagerController {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    AppManagerService appManagerService;

    @GetMapping("/queryAppManager")
    public Response queryAppManager(AppManagerModel model, HttpServletRequest request) {


        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }

        return Response.success(appManagerService.queryAppManager(model));
    }

    @GetMapping("/queryAppManagerList")
    public Response queryAppManagerList(AppManagerModel model, HttpServletRequest request) {


        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }
        model.setStatus(CommonConst.STATUS_VALID);

        return Response.success(appManagerService.queryAppManager(model));
    }


    @PostMapping("/addAppManager")
    public Response addAppManager(AppManagerModel model, HttpServletRequest request) {
        checkBaseParam(model);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }
//        Integer counts = appManagerService.findAppManagerByAgencyId(model.getAgencyId(), model.getPlatform());
//        if (counts > 0) {
//            throw new ResolveMsgException("common.param.repeat", "平台");
//        }
        appManagerService.addAppManager(model);
        return Response.success();
    }


    @PostMapping("/updateAppManager")
    public Response updateAppManager(AppManagerModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), "id不能为空");
        checkBaseParam(model);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }

        appManagerService.updateAppManager(model);
        return Response.success();
    }

    private void checkBaseParam(AppManagerModel model) {
        Assert.notNull(model.getVestId(), "马甲名称不能为空");
        Assert.notNull(model.getPlatform(), "平台字段不能为空");
        Assert.notNull(model.getStatus(), "状态不能为空");
        Assert.notNull(model.getLogoUrl(), "logo地址不能为空");
        Assert.notNull(model.getName(), "名字不能为空");
        Assert.notNull(model.getCustomerQq(), "客服QQ不能为空");
        Assert.notNull(model.getCustomerWechat(), "客服微信不能为空");
        Assert.notNull(model.getCopyRight(), "版权不能为空");
    }

}
