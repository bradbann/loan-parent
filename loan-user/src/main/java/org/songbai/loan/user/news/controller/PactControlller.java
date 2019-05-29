package org.songbai.loan.user.news.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.news.service.PactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pact")
@LimitLess
public class PactControlller {
    @Autowired
    ComAgencyService comAgencyService;
    @Autowired
    PactService pactService;

    @GetMapping(value = "/getPactInfo")
    public Response getPactInfo(String vestCode, HttpServletRequest request) {
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        AppVestModel vestModel = comAgencyService.findVestByIdOrVestCode(agencyId, null, vestCode);
        if (vestModel == null || StringUtils.isEmpty(vestModel.getPactId())) return Response.success();

        return Response.success(pactService.getPactInfoById(vestModel.getPactId()));
    }
}
