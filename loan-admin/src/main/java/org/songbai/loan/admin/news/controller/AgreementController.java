package org.songbai.loan.admin.news.controller;

import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.service.AgreementService;
import org.songbai.loan.model.news.AgreementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 文章管理
 *
 * @date 2018年10月31日 14:46:22
 * @description
 */
@RestController
@RequestMapping("/agreement")
public class AgreementController {
    @Autowired
    private AdminUserHelper adminUserHelper;

    @Autowired
    private AgreementService agreementService;

    @GetMapping(value = {"/findAgreementByPage"})
    @ResponseBody
    public Response findAgreementByPage(AgreementModel agreementModel, Integer page, Integer pageSize, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        if (agreementModel == null) {
            agreementModel = new AgreementModel();
        }

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;

        Page<AgreementModel> pageResult = agreementService.findAgreementByPage(agreementModel, page, pageSize, userModel.getDataId());
        return Response.success(pageResult);
    }


    @GetMapping(value = {"/findAgreement"})
    @ResponseBody
    public Response findAgreement(String id) {

        if (StringUtil.isEmpty(id)) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        AgreementModel result = agreementService.findAgreementById(id);

        return Response.success(result);
    }


    @PostMapping(value = {"/saveAgreement"})
    @ResponseBody
    public Response saveAgreement(AgreementModel agreementModel, HttpServletRequest request) {
        Assert.notNull(agreementModel, "参数不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        agreementModel.setOperator(userModel.getName());
        agreementModel.setAgencyId(userModel.getDataId());

        agreementService.saveAgreement(agreementModel);
        return Response.success();
    }

    @PostMapping(value = {"/updateAgreement"})
    @ResponseBody
    public Response updateAgreement(AgreementModel agreementModel, HttpServletRequest request) {

        if (agreementModel == null || agreementModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        agreementModel.setOperator(userModel.getName());

        agreementService.updateAgreement(agreementModel);
        return Response.success();
    }

    @PostMapping(value = {"/deleteAgreement"})
    @ResponseBody
    public Response deleteAgreement(String ids, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        agreementService.deleteAgreement(ids, userModel.getDataId());

        return Response.success();
    }


}
