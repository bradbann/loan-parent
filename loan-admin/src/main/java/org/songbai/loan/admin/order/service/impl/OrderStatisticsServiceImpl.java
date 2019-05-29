package org.songbai.loan.admin.order.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.service.OrderStatisticsService;
import org.songbai.loan.admin.user.dao.UserDao;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户详情中的订单统计
 * @author wjl
 * @date 2018年11月07日 18:39:48
 * @description
 */
@Service
public class OrderStatisticsServiceImpl implements OrderStatisticsService{

	@Autowired
	private UserDao userDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private ComUserService comUserService;

	/**
	 * 用户详情中订单统计
	 */
	@Override
	public Map<String, Integer> OrderStatistics(String thirdId,Integer agencyId, String idcardNum) {
		UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
		if (userModel == null){
			return new HashMap<>();
		}
		Integer userId = userModel.getId();
		Map<String, Integer> map = new HashMap<>();
		Map<String, Integer> result = new HashMap<>();
		List<Integer> list;
		String userIds = "";
		Integer totalCount = 0,delayCount = 0,allPlatform = 0,allDelayPlatform = 0,finishOrder = 0,refuseCount = 0,windRefuseCount = 0,temp = 0;
		if (agencyId == 0 && StringUtils.isNotBlank(idcardNum)) {//平台用户
			list = userDao.getUserIdByIdcardNum(idcardNum);
			userIds =StringUtils.join(list,",");
//			在借平台数
			map.put("flag", 1);
			allPlatform = orderDao.getOrderCountByUserId(userIds, map);
//			逾期平台数
			map.put("exceedDays", 1);
			allDelayPlatform = orderDao.getOrderCountByUserId(userIds, map);
			
		}else {//代理用户
			userIds = userId+"";
//			在借平台数
			temp = orderDao.getOrderCountByUserId(userIds, new HashMap<>());
			if (temp > 0) {
				allPlatform = 1;
			}
//			逾期平台数
			map.put("exceedDays", 1);
			allDelayPlatform = orderDao.getOrderCountByUserId(userIds, map);
		}
//		提单总数-当前平台提单总数
		totalCount = temp;
//		催收订单数
		map.clear();
		map.put("autoPayment", 1);
		delayCount = orderDao.getOrderCountByUserId(userIds, map);
//		订单完成数
		map.clear();
		map.put("stage", 4);
		finishOrder = orderDao.getOrderCountByUserId(userIds, map);
//		复审拒绝数
		map.put("stage", 2);
		map.put("status", 3);
		refuseCount = orderDao.getOrderCountByUserId(userIds, map);
//		风控拒绝数
		map.put("stage", 1);
		map.put("status", 3);
		windRefuseCount = orderDao.getOrderCountByUserId(userIds, map);
		
		result.put("totalCount", totalCount);
		result.put("delayCount", delayCount);
		result.put("allPlatform", allPlatform);
		result.put("allDelayPlatform", allDelayPlatform);
		result.put("finishOrder", finishOrder);
		result.put("refuseCount", refuseCount);
		result.put("windRefuseCount", windRefuseCount);
		return result;
	}
	
}
