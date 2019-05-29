package org.songbai.loan.admin.channel.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.channel.service.ActorChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/actorChannel")
public class ActorChannelCtrl {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    ActorChannelService actorChannelService;

    /**
     * 用户已关联渠道
     */
    @GetMapping("/findActorManagerList")
    public Response findActorManagerList(Integer actorId, HttpServletRequest request) {
        Assert.notNull(actorId, "用户不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        return Response.success(actorChannelService.findActorManagerList(agencyId, actorId));
    }

    @GetMapping("/findManageChannelList")
    public Response findManageChannelList(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);

        return Response.success(actorChannelService.findActorManagerList(agencyId, actorId));
    }

    /**
     * 用户渠道关联赋权
     */
    @PostMapping("/grantActorChannel")
    public Response grantActorChannel(Integer actorId, HttpServletRequest request, String channelIds) {
        Assert.notNull(actorId, "用户不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 18;
        if (StringUtils.isEmpty(channelIds)) {
            actorChannelService.deleteByActorId(actorId);
        } else {
            actorChannelService.grantActorChannel(agencyId, actorId, channelIds);
        }

        return Response.success();
    }


}
