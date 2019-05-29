package org.songbai.loan.user.user.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.loan.common.helper.LimitRequestHelper;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.user.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: qmw
 * Date: 2018/10/31 3:07 PM
 */
@RestController
@RequestMapping("/order")
public class OrderCtrl {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    private LimitRequestHelper limitRequestHelper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @LimitLess
    @GetMapping("/home")
    public Response home(HttpServletRequest request) {
        Integer userId = UserUtil.getUserId();
        return Response.success(orderService.loanHome(UserUtil.getUserId(), request));
    }

    @PostMapping("/loan")
    public Response loan(Double loan) {
        try {
            Integer userId = UserUtil.getUserId();
            limitRequestHelper.validateUserRequest(UserRedisKey.USER_ORDER_LIMIT, userId, 3L);
            orderService.loan(userId, loan);
        } finally {
            redisTemplate.delete(UserRedisKey.USER_ORDER_LIMIT + UserUtil.getUserId());
        }
        return Response.success();
    }

    @GetMapping("/loan/detail")
    public Response loanDetail(Double loan, HttpServletRequest request) {
        Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

        return Response.success(orderService.loanDetail(UserUtil.getUserId(), agencyId, loan));
    }

    @GetMapping("/list")
    public Response list(PageRow pageRow) {
        Integer userId = UserUtil.getUserId();
        pageRow.initLimit();
        return Response.success(orderService.orderList(UserUtil.getUserId(), pageRow));
    }

    @GetMapping("/detail")
    public Response detail(String orderNumber) {
        Assert.hasLength(orderNumber, LocaleKit.get("common.param.notnull", "orderNumber"));
        Integer userId = UserUtil.getUserId();

        return Response.success(orderService.orderDetailByOrderNumber(userId, orderNumber));
    }

}
