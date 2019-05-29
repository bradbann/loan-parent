package org.songbai.loan.admin.order.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.po.*;
import org.songbai.loan.admin.order.service.OrderService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 放款相关操作
 * Author: qmw
 * Date: 2018/11/5 4:42 PM
 */
@RestController
@RequestMapping("/order")
public class OrderPaymentCtrl {
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private OrderService orderService;

    /**
     * 放款列表
     */
    @GetMapping("/payment/list")
    public Response paymentList(OrderPaymentPO po, HttpServletRequest request, PageRow pageRow) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }

        po.setAgencyId(agencyId);
        pageRow.initLimit();
        return Response.success(orderService.paymentList(pageRow, po));
    }

    /**
     * 放款订单退回
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/payment/return")
    public Response paymentReturn(String orderNumber, String remark, HttpServletRequest request) {
        Assert.hasLength(orderNumber, LocaleKit.get("common.param.notnull", "orderNumber"));
        Assert.hasLength(remark, LocaleKit.get("common.param.notnull", "remark"));
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);

        orderService.paymentReturn(orderNumber, remark, actorId, agencyId);
        return Response.success();
    }

    /**
     * 拒绝
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/payment/reject")
    public Response reject(@RequestParam("ids") List<String> ids, String remark, Date againDate, HttpServletRequest request) {
        if (ids.isEmpty()) {
            Assert.notNull(ids, LocaleKit.get("common.param.notnull", "ids"));
        }
        Assert.hasLength(remark, LocaleKit.get("common.param.notnull", "remark"));
        Assert.notNull(againDate, LocaleKit.get("common.param.notnull", "againDate"));
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        //Integer agencyId = 0;
        //Integer actorId = 1;
        orderService.rejectPay(ids, agencyId, actorId, remark, againDate);
        return Response.success();
    }

    /**
     * 放款统计
     */
    @GetMapping("/payment/statistics")
    public Response paymentStatistics(HttpServletRequest request) {
        //Integer agencyId = adminUserHelper.getAgencyId(request);
        //如果是云平台  agencyId是空的
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        return Response.success(orderService.paymentStatistics(agencyId, actorId));
    }

    /**
     * 放款记录
     */
    @GetMapping("/payment/record")
    public Response paymentRecord(PaymentRecordPO po, HttpServletRequest request, PageRow pageRow) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) po.setAgencyId(agencyId);

        pageRow.initLimit();
        String startTime = po.getStartDate();
        String endTime = po.getEndDate();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartDate(startTime +" 00:00:00");
            po.setEndDate(endTime + " 23:59:59");
        }
        return Response.success(orderService.paymentRecordList(pageRow, po));
    }

    /**
     * 还款列表
     */
    @GetMapping("/repay/list")
    public Response reapyList(RepayListPO po, HttpServletRequest request, PageRow pageRow) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) po.setAgencyId(agencyId);
        pageRow.initLimit();
        return Response.success(orderService.repayList(pageRow, po));
    }

    /**
     * 线下还款确认
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/repay/confirm")
    public Response reapyConfirm(RepayPO po, HttpServletRequest request) {
        Assert.hasLength(po.getOrderNumber(), LocaleKit.get("common.param.notnull", "orderNumber"));
        Assert.hasLength(po.getRepayType(), LocaleKit.get("common.param.notnull", "repayType"));
        Assert.hasLength(po.getReceipt(), LocaleKit.get("common.param.notnull", "receipt"));
        Assert.notNull(po.getPayment(), LocaleKit.get("common.param.notnull", "payment"));
        Assert.notNull(po.getRepaymentTime(), LocaleKit.get("common.param.notnull", "repaymentTime"));

        if (po.getPayment() <= 0) {
            throw new BusinessException(AdminRespCode.MONEY_THAN_ZERO);
        }

        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        po.setAgencyId(agencyId);
        po.setActorId(actorId);
        orderService.repayConfirm(po);
        return Response.success();
    }

    /**
     * 减免金额
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/repay/deduct")
    public Response reapyDedeuct(String orderNumber, Double deductMoney, String remark, HttpServletRequest request) {
        Assert.hasLength(orderNumber, LocaleKit.get("common.param.notnull", "orderNumber"));
        Assert.hasLength(remark, LocaleKit.get("common.param.notnull", "remark"));
        Assert.notNull(deductMoney, LocaleKit.get("common.param.notnull", "deductMoney"));


        if (deductMoney <= 0) {
            throw new BusinessException(AdminRespCode.MONEY_THAN_ZERO);
        }

        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);

        orderService.repayDeduct(orderNumber, deductMoney, agencyId, actorId, remark);
        return Response.success();
    }

    /**
     * 还款记录
     */
    @GetMapping("/repay/record")
    public Response repayRecord(RepaymentRecordPO po, HttpServletRequest request, PageRow pageRow) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        pageRow.initLimit();
        String startTime = po.getStartDate();
        String endTime = po.getEndDate();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartDate(startTime +" 00:00:00");
            po.setEndDate(endTime + " 23:59:59");
        }
        return Response.success(orderService.repaymentRecordList(pageRow, po));
    }
}
