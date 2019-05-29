package org.songbai.loan.admin.order.service;

import java.util.Map;

public interface OrderStatisticsService {

	Map<String, Integer> OrderStatistics(String thirdId,Integer agencyId,String idcardNum);
}
