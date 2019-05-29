package org.songbai.loan.admin.news.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.service.BannerService;
import org.songbai.loan.model.news.BannerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * banner 管理
 *
 * @date 2018年10月31日 14:47:07
 * @description
 */
@RestController
@RequestMapping("banner")
public class BannerController {
    private Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    private BannerService bannerService;
    @Autowired
    private AdminUserHelper adminUserHelper;

    @RequestMapping(value = {"/findbannerByPage"})
    public Response findBannerByPage(BannerModel bannerModel, Integer page, Integer pageSize, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (bannerModel == null) {
            bannerModel = new BannerModel();
        }

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        if (userModel.getDataId() != 0) {
            bannerModel.setAgencyId(userModel.getDataId());
        }
        Page<BannerModel> pageResult = bannerService.findBannerByPage(bannerModel, page, pageSize, userModel.getDataId());
        return Response.success(pageResult);
    }


    @GetMapping(value = {"/findbanner"})
    public Response findBanner(BannerModel bannerModel) {

        if (bannerModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        return Response.success(bannerService.findBanner(bannerModel));
    }

    /**
     * banner新增
     */
    @RequestMapping(value = {"/savebanner"})
    public Response saveBanner(BannerModel bannerModel, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        bannerModel.setOperator(userModel.getName());
        if (userModel.getDataId() != 0) {
            bannerModel.setAgencyId(userModel.getDataId());
        }
        bannerService.saveBanner(bannerModel);
        return Response.success();
    }

    @RequestMapping(value = {"/updateBanner"})
    public Response updateBanner(BannerModel bannerModel, HttpServletRequest request) {
        if (bannerModel == null || bannerModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (userModel.getDataId() != 0) {
            bannerModel.setAgencyId(userModel.getDataId());
        }
        bannerModel.setOperator(userModel.getName());

        bannerService.updateBanner(bannerModel, userModel);
        return Response.success();
    }

    @RequestMapping(value = {"/deleteBanner"})
    public Response deleteBanner(String ids, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        bannerService.deleteBanner(ids, userModel.getDataId());

        return Response.success();
    }


    @RequestMapping(value = {"/updateStatus"})
    public Response updateStatus(BannerModel bannerModel, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        bannerModel.setOperator(userModel.getName());
        bannerService.updateBannerStatus(bannerModel);
        return Response.success();
    }


    @PostMapping(value = {"/pushMsg"})
    public Response pushMsg(String id, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        bannerService.pushMsg(id, agencyId);
        return Response.success();
    }

}