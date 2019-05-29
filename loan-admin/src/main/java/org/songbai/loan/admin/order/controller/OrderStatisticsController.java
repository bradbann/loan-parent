package org.songbai.loan.admin.order.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.service.OrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 订单统计相关
 * @author wjl
 * @date 2018年11月07日 15:25:01
 * @description
 */
@RestController
@RequestMapping("/order")
public class OrderStatisticsController {

	@Autowired
	private AdminUserHelper userHelper;
	@Autowired
	private OrderStatisticsService orderStatisticsService;
	
	@GetMapping("/statistics")
	public Response statistics(String userId,String idcardNum,HttpServletRequest request){
		Integer agencyId = userHelper.getAgencyId(request);

		return Response.success(orderStatisticsService.OrderStatistics(userId, agencyId, idcardNum));
	}
	
}
