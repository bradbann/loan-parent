package org.songbai.loan.admin.chase.controller;


import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.chase.po.ChaseDebtPo;
import org.songbai.loan.admin.chase.service.ChaseService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.agency.AgencyDeptConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 催收
 */
@RestController
@RequestMapping("/chase")
public class ChaseController {

    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private ChaseService chaseService;
    @Autowired
    private AdminDeptService adminDeptService;

    /**
     * 催收分配分页
     */
    @GetMapping("/getChasePage")
    public Response getChasePage(ChaseDebtPo po, HttpServletRequest request) {
//        Integer agencyId = 0;
        Integer agencyId = adminUserHelper.getAgencyId(request);
        po.setAgencyId(agencyId);

        po.initLimit();

        return Response.success(chaseService.getChasePage(po));
    }

    /**
     * 催收分配导出
     */
    @GetMapping("/exportChasePage")
    @ResponseBody
    public Response exportChasePage(ChaseDebtPo po, HttpServletRequest request, HttpServletResponse response) {
//        Integer agencyId = 0;
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();

        chaseService.exportChasePage(po, response);
        return Response.success();
    }

    /**
     * 获取催收小组
     */
    @GetMapping(value = "/getChaseDeptList")
    public Response getChaseDeptList(HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer deptType = AgencyDeptConstant.DeptType.CHASEDEBT_DEPT.key;

        return Response.success(adminDeptService.findDeptListByType(userModel, deptType));
    }

    /**
     * 催收分配
     */
    @PostMapping(value = "/seperateOrder")
    @Accessible(onlyAgency = true)
    public Response seperateOrder(HttpServletRequest request, String orderNumbers, Integer deptId) {
        Assert.notNull(orderNumbers, "订单编号不能为空");
        Assert.notNull(deptId, "分配部门不能为空");
        Integer actorId = adminUserHelper.getAdminUserId(request);
        chaseService.seperateOrder(orderNumbers, deptId, actorId);
        return Response.success();
    }

    /**
     * 设为坏账
     */
    @PostMapping(value = "/doBadDebt")
    @Accessible(onlyAgency = true)
    public Response doBadDebt(HttpServletRequest request, String orderNumber) {
        Assert.notNull(orderNumber, "订单编号不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        chaseService.doBadDebt(orderNumber, agencyId, userModel.getId());
        return Response.success();
    }

    /**
     * 组内分配--分页
     */
    @GetMapping("/getGroupChasePage")
    public Response getGroupChasePage(ChaseDebtPo po, HttpServletRequest request) {
//        Integer agencyId = 0;
        Integer agencyId = adminUserHelper.getAgencyId(request);
        po.setAgencyId(agencyId);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        po.initLimit();

        return Response.success(chaseService.getGroupChasePage(po, userModel));
    }

    /**
     * 获取催收组内成员
     */
    @GetMapping(value = "/getChaseDeptActor")
    public Response getChaseDeptActor(HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer deptType = AgencyDeptConstant.DeptType.CHASEDEBT_DEPT.key;

        return Response.success(chaseService.getChaseDeptActor(userModel, deptType));
    }

    /**
     * 组内分单
     */
    @PostMapping(value = "/groupSeperateOrder")
    @Accessible(onlyAgency = true)
    public Response groupSeperateOrder(HttpServletRequest request, String orderNumbers, Integer actorId) {
        Assert.notNull(orderNumbers, "订单编号不能为空");
        Assert.notNull(actorId, "分配人员不能为空不能为空");
        Integer currentActorId = adminUserHelper.getAdminUserId(request);
        chaseService.groupSeperateOrder(orderNumbers, actorId, currentActorId);
        return Response.success();
    }

    /**
     * 我的催单分页
     */
    @GetMapping("/getOwnerChasePage")
    public Response getOwnerChasePage(ChaseDebtPo po, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (!userModel.getRoleType().equals(1)) {
            po.setChaseActorId(userModel.getId());
        }
        if (userModel.getDataId() != 0)
            po.setAgencyId(userModel.getDataId());

        po.initLimit();

        return Response.success(chaseService.getOwnerChasePage(po));
    }

    @GetMapping("/exportOwnerChase")
    @ResponseBody
    public Response exportOwnerChase(ChaseDebtPo po, HttpServletRequest request, HttpServletResponse response) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (!userModel.getRoleType().equals(1)) {
            po.setChaseActorId(userModel.getId());
        }

        if (userModel.getDataId() != 0)
            po.setAgencyId(userModel.getDataId());

        chaseService.exportOwnerChasePage(po, response);
        return Response.success();
    }

}
