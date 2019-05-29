package org.songbai.loan.admin.news.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.service.PactService;
import org.songbai.loan.model.news.PactModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 协议管理
 */
@RestController
@RequestMapping("/pact")
public class PactController {
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    PactService pactService;

    @GetMapping(value = {"/findPactPage"})
    public Response findPactPage(PactModel param, Integer page, Integer pageSize, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) param.setAgencyId(agencyId);
        if (param == null) {
            param = new PactModel();
        }

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;

        return Response.success(pactService.findPactPage(param, page, pageSize));
    }


    @GetMapping(value = {"/findPactList"})
    public Response findPactList(PactModel param, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) param.setAgencyId(agencyId);
        if (param == null) {
            param = new PactModel();
        }
        return Response.success(pactService.findPactList(param));
    }

    @GetMapping(value = {"/findPactById"})
    public Response findPactById(String id) {
        Assert.notNull(id, "id不能为空");
        return Response.success(pactService.findPactById(id));
    }


    @PostMapping(value = {"/addPact"})
    public Response addPact(PactModel pactModel, HttpServletRequest request) {
        checkBaseParam(pactModel);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer agencyId = userModel.getDataId();
        if (agencyId != 0) pactModel.setAgencyId(agencyId);
        pactModel.setOperator(userModel.getName());
        pactModel.setId(null);
        pactService.addPact(pactModel);
        return Response.success();
    }


    @PostMapping(value = {"/updatePact"})
    public Response updatePact(PactModel model, HttpServletRequest request) {
        checkBaseParam(model);
        Assert.notNull(model.getId(), "id不能为空");

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer agencyId = userModel.getDataId();
        if (agencyId != 0) model.setAgencyId(agencyId);
        model.setOperator(userModel.getName());

        pactService.updatePact(model);
        return Response.success();
    }

    @PostMapping(value = {"/deletePact"})
    public Response deletePact(String ids, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        pactService.deletePact(ids, userModel.getDataId());

        return Response.success();
    }



    private void checkBaseParam(PactModel pactModel) {
        Assert.notNull(pactModel, "参数不能为空");
        Assert.notNull(pactModel.getCode(), "code不能为空");
        Assert.notNull(pactModel.getTitle(), "标题不能为空");
        Assert.notNull(pactModel.getType(), "类型不能为空");
    }


}
