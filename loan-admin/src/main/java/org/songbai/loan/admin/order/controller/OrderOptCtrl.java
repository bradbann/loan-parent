package org.songbai.loan.admin.order.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.po.OrderOptPo;
import org.songbai.loan.admin.order.service.OrderOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/orderOpt")
public class OrderOptCtrl {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    OrderOptService orderOptService;

    @GetMapping(value = "/getOrderOptPage")
    public Response getOrderOptPage(OrderOptPo po, HttpServletRequest request, Integer page, Integer pageSize) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        String startTime = po.getStartDate();
        String endTime = po.getEndDate();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartDate(startTime +" 00:00:00");
            po.setEndDate(endTime + " 23:59:59");
        }
        return Response.success(orderOptService.getOrderOptPage(po));
    }

    @GetMapping(value = "/list")
    public Response list(String orderNumber, HttpServletRequest request) {
        Assert.hasLength(orderNumber, "订单号不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) {
            agencyId = null;
        }
        return Response.success(orderOptService.findOptList(agencyId, orderNumber));
    }

}
