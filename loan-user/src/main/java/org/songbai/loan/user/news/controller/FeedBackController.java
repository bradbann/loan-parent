package org.songbai.loan.user.news.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.news.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/feedBack")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private ComAgencyService comAgencyService;

    @PostMapping("/commit")
    public Response commitBack(String content, MultipartFile[] files, HttpServletRequest request) {
        Assert.hasLength(content, "内容不能为空");
        if (content != null) {
            if (files.length > 3) {
                return Response.response(0, "截图最多3张");
            }
        }
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
        String vestCode = PlatformKit.parseChannel(request);
        Integer vestId = comAgencyService.findVestIdByVestCode(agencyId, vestCode);
        feedBackService.commitBack(content, files, vestId);
        return Response.success();
    }
}
