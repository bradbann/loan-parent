package org.songbai.loan.admin.order.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.service.OrderOptService;
import org.songbai.loan.admin.order.service.OrderService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.agency.AgencyDeptConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 信审工作台
 */
@RestController
@RequestMapping("/order")
public class OrderCtrl {

    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderOptService orderOptService;
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    SpringProperties properties;
    @Autowired
    AdminDeptService adminDeptService;

    /**
     * 后台订单列表
     */
    @GetMapping("/list")
    public Response getOrderList(OrderPo po, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) po.setAgencyId(agencyId);
        po.initLimit();
        String startTime = po.getStartDate();
        String endTime = po.getEndDate();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartDate(startTime +" 00:00:00");
            po.setEndDate(endTime + " 23:59:59");
        }
        return Response.success(orderService.orderList(po));
    }

    @GetMapping("/exportOrderList")
    public Response exportOrderList(OrderPo po, HttpServletRequest request, HttpServletResponse response) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) po.setAgencyId(agencyId);
        orderService.exportOrderList(po, response);
        return Response.success();
    }


    /**
     * 获取待复审订单
     */
    @GetMapping(value = "/getWaitReviceOrderPage")
    @ResponseBody
    public Response getWaitReviceOrderPage(OrderPo po, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        return Response.success(orderService.getWaitReviceOrderPage(po));
    }

    /**
     * 我的记录
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getOwnerRecord")
    public Response getOwnerRecord(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        return Response.success(orderOptService.getOwnerRecord(agencyId, actorId));
    }


    @PostMapping(value = "/takeOrder")
    @ResponseBody
    @Accessible(onlyAgencyCommon = true)
    public Response takeOrder(Integer count, HttpServletRequest request) {
        Assert.notNull(count, "取单数不能为空");
        Integer takeCount = properties.getInteger("admin.order.take.count", 10);
        Assert.isTrue(0 < count && count < takeCount + 1, "取单数只允许在1~" + takeCount + "范围内");

        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        return Response.success(orderService.takeOrder(count, agencyId, actorId));
    }

    /**
     * 获取自己的待审核的订单
     */
    @GetMapping(value = "/getOwnerReviceOrder")
    @ResponseBody
    public Response getOwnerReviceOrder(OrderPo po, HttpServletRequest request) {
        AdminUserModel user = adminUserHelper.getAdminUser(request);
        Integer agencyId = user.getDataId();
        Integer actorId = user.getId();

        return Response.success(orderService.getOwnerReviceOrder(po, agencyId, actorId));
    }


    @PostMapping(value = "/updateOrderAuthStatus")
    @ResponseBody
    @Accessible(onlyAgency = true)
    public Response updateOrderAuthStatus(String orderNumber, Integer orderStatus, String remark, HttpServletRequest request) {
        Assert.notNull(orderNumber, "订单号不能为空");
        Assert.notNull(orderStatus, "审核结果不能为空");
        AdminUserModel user = adminUserHelper.getAdminUser(request);
        Integer agencyId = user.getDataId();
        Integer actorId = user.getId();
        orderService.updateOrderAuthStatus(orderNumber, agencyId, actorId, orderStatus, remark);
        return Response.success();
    }

    /**
     * 复审人员名单
     */
    @GetMapping(value = "/getReviewDeptList")
    public Response getReviewDeptList(HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer deptType = AgencyDeptConstant.DeptType.REVIEW_DEPT.key;
        List<Integer> deptIds = adminDeptService.findAllDeptIdsByType(userModel.getDataId(), deptType);

        return Response.success(adminUserService.findUserListByDeptIds(deptIds, userModel.getDataId()));
    }

    /**
     * 根据某一用户查询未处理订单号
     */
    @GetMapping(value = "/getReviceOrderList")
    public Response getReviceOrderList(Integer actorId, HttpServletRequest request) {
        Assert.notNull(actorId, "用户id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        return Response.success(orderService.getOwnerReviceOrder(new OrderPo(), agencyId, actorId));
    }

    /**
     * 退单
     */
    @PostMapping(value = "/returnReviewOrder")
    @Accessible(onlyAgency = true)
    public Response returnReviewOrder(Integer actorId, String orderNumber, HttpServletRequest request) {
        Assert.notNull(orderNumber, "订单号不能为空");

        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer opeartor = adminUserHelper.getAdminUserId(request);
        return Response.success(orderService.returnReviewOrder(actorId, agencyId, orderNumber, opeartor));
    }


}
