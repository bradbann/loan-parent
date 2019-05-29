package org.songbai.loan.admin.channel.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.channel.model.po.ChannelQueryPo;
import org.songbai.loan.admin.channel.service.ChannelService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    ChannelService channelService;
    @Autowired
    ComUserService comUserService;

    @GetMapping("/findAgencyChannelPage")
    public Response findAgencyChannelPage(AgencyChannelModel model, HttpServletRequest request,
                                          @RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "20") Integer pageSize) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (userModel.getDataId() != 0)
            model.setAgencyId(userModel.getDataId());

        return Response.success(channelService.findAgencyChannelPage(model, userModel, page, pageSize));
    }

    @GetMapping("/findChannelList")
    public Response findChannelList(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;
        return Response.success(channelService.findChannelList(agencyId));
    }

    @GetMapping("/safe_findChannelCodeList")
    public Response findChannelCodeList(HttpServletRequest request, Integer agencyId, Integer vestId) {
        Integer superAgencyId = adminUserHelper.getAgencyId(request);
        if (superAgencyId != 0) {
            agencyId = superAgencyId;
        }

        return Response.success(comUserService.findChannelCodeList(agencyId, vestId));
    }

    @PostMapping("/addChannel")
    public Response addChannel(AgencyChannelModel model, HttpServletRequest request) {
        Assert.notNull(model.getShowPercent(), "展现百分比不能为空");
        Assert.notNull(model.getChannelCode(), "code不能为空");
        Assert.isTrue(0D < model.getShowPercent() && model.getShowPercent() <= 100D, "展现百分比只能在0~100之间");
        Assert.notNull(model.getVestId(), "马甲不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }
        channelService.addChannel(model);
        return Response.success();
    }

//    @Accessible(onlyAgency = true)
    @PostMapping("/upateChannel")
    public Response upateChannel(AgencyChannelModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), "id不能为空");
        if (model.getId().equals(0)) return Response.error();
        Assert.notNull(model.getShowPercent(), "手续费利率不能为空");
        Assert.isTrue(0D < model.getShowPercent() && model.getShowPercent() <= 100D, "展现百分比只能在0~100之间");
        Assert.notNull(model.getVestId(), "马甲不能为空");

        model.setChannelCode(null);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }
        channelService.upateChannel(model);
        return Response.success();
    }

    @GetMapping("/findInfoById")
    public Response findInfoById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return Response.success(channelService.findInfoById(id));
    }

    /**
     * 我的客户--分页
     */
    @GetMapping("/findMyCustomerPage")
    public Response findMyCustomerPage(ChannelQueryPo po, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDataId(18);
//        userModel.setId(39);
        po.initLimit();
        return Response.success(channelService.findMyCustomerPage(po, userModel));
    }


    /**
     * 查询当前代理下的设置的渠道
     */
    @GetMapping("/list")
    public Response findChannel(HttpServletRequest request) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0){
            agencyId = null;
        }

        return Response.success(channelService.findChannelByAgencyId(agencyId));
    }

}
