package org.songbai.loan.admin.user.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.user.service.CallReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/callReport")
public class CallReportCtrl {

    @Autowired
    private AdminUserHelper adminUserHelper;

    @Autowired
    private CallReportService callReportService;

    @GetMapping("indexReport")
    public Response indexReport(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        return Response.success(callReportService.callReport(userId, agencyId, 0, 100));
    }

    @GetMapping("voiceCall")
    public Response voiceCall(String userId, String dialType, Integer page, Integer pageSize, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 20 : pageSize;

        return Response.success(callReportService.queryVoiceCall(userId, dialType, agencyId, page, pageSize));
    }

    @GetMapping("mobileSms")
    public Response mobileSms(String userId, String sendType, Integer page, Integer pageSize, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 20 : pageSize;


        return Response.success(callReportService.queryMobileSms(userId, agencyId, sendType, page, pageSize));
    }
}
