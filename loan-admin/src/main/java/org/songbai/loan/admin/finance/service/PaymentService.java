package org.songbai.loan.admin.finance.service;

import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;

import java.util.List;

public interface PaymentService {

	/**
	 * 单笔放款校验(包含商户账户余额校验)
	 */
	List<Integer> validate(List<OrderModel> orderModels, Integer agencyId, Integer actorId);

	/**
	 * 单笔放款接口
	 */
	void transfer(List<Integer> list, Integer agencyId, Integer actorId);

	PlatformConfig getConfig(Integer agencyId);

	String getCode();

}
