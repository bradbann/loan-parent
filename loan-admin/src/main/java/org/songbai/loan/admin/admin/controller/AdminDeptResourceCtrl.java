package org.songbai.loan.admin.admin.controller;


import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequestMapping("deptResource")
public class AdminDeptResourceCtrl {

    @Autowired
    AdminRoleService adminRoleService;
    @Autowired
    AdminMenuResouceService adminMenuResouceService;
    @Autowired
    AdminUserHelper adminUserHelper;

    @Autowired
    AdminDeptService adminDeptService;

    /**
     * 部门授权
     */
    @PostMapping(value = "/grantResourcesToDept")
    public Response grantResourcesToDept(Integer deptId, String securityResourceIds, HttpServletRequest request) {
        Assert.notNull(deptId, "部门id不能为空！");
        if (securityResourceIds.equals("")) {
            adminDeptService.deleteResourceByDeptId(deptId);
        } else {

            Integer[] resourceIds = StringUtil.split2Int(securityResourceIds);

            adminDeptService.saveResourceToDeptId(deptId, Arrays.asList(resourceIds), adminUserHelper.getAgencyId(request));
        }
        return Response.success();
    }


    @GetMapping(value = "/getAllMenuPageUrl")
    public Response getAllMenuPageUrl(Integer deptId, HttpServletRequest request) {
        Assert.notNull(deptId, "部门id不能为空！");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        return Response.success(adminDeptService.getAllMenuPageUrl(deptId, userModel));
    }
}

