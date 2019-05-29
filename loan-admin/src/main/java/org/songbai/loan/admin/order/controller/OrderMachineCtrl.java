package org.songbai.loan.admin.order.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.service.OrderMachineService;
import org.songbai.loan.config.Accessible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 机审转复审订单
 */
@RestController
@RequestMapping("/orderMachine")
public class OrderMachineCtrl {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    OrderMachineService orderMachineService;

    /**
     * 机审失败转复审订单
     */
    @GetMapping(value = "/findMachineFailPage")
    public Response findMachineFailPage(OrderPo po, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        return Response.success(orderMachineService.findMachineFailPage(po));
    }

    @PostMapping(value = "/updateMachineOrderStatus")
    @Accessible(onlyAgency = true)
    public Response updateMachineOrderStatus(String orderNumber, HttpServletRequest request) {
        Assert.notNull(orderNumber, "订单编号不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        orderMachineService.updateMachineOrderStatus(orderNumber, userModel.getDataId(), userModel.getId());
        return Response.success();
    }
}
