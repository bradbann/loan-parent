package org.songbai.loan.admin.schdule.listener;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.admin.agency.dao.AgencyDao;
import org.songbai.loan.admin.finance.service.PaymentService;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wjl
 * @date: 2018/12/27 12:02
 * Description: 自动放款
 */
@Component
public class AutoTransferListener {
	private static final Logger log = LoggerFactory.getLogger(AutoTransferListener.class);

	@Autowired
	private AgencyDao agencyDao;
	@Autowired
	private ComFinanceService comFinanceService;
	@Autowired
	private OrderDao orderDao;
	@Resource(name = "changJiePaymentService")
	private PaymentService changJiePaymentService;
	@Resource(name = "yiBaoPaymentService")
	private PaymentService yiBaoPaymentService;
	@Resource(name = "testPaymentService")
	private PaymentService testPaymentService;

	@JmsListener(destination = JmsDest.AUTO_TRANSFER)
	public void autoTransfer() {
		//先查那些代理启用了自动放款
		List<Integer> agencyIds = agencyDao.selectEnableAutoPayAgency();
		log.info("查询到当前开启自动放款的代理有：{}", agencyIds);
		//根据代理去取配置和订单
		for (Integer agencyId : agencyIds) {
			try {
				List<OrderModel> orderModels = orderDao.selectWaitTransferOrderByAgencyId(agencyId);
				//查询10分钟一次而且待放款4 1 的订单
				String platformCode = comFinanceService.getPayCodeByAgency(agencyId);
				log.info("代理：{}开始使用{}平台自动放款{}笔订单",agencyId, FinanceConstant.PayPlatform.getName(platformCode),orderModels.size());
				if (platformCode.equals(FinanceConstant.PayPlatform.CHANGJIE.code)) {
					List<Integer> list = changJiePaymentService.validate(orderModels, agencyId, null);
					if (CollectionUtils.isEmpty(list)) {
						continue;
					}
					changJiePaymentService.transfer(list, agencyId, null);
				}
				if (platformCode.equals(FinanceConstant.PayPlatform.TEST.code)) {
					List<Integer> list = testPaymentService.validate(orderModels, agencyId, null);
					if (CollectionUtils.isEmpty(list)) {
						continue;
					}
					testPaymentService.transfer(list, agencyId, null);
				}
				if (platformCode.equals(FinanceConstant.PayPlatform.YIBAO.code)) {
					List<Integer> list = yiBaoPaymentService.validate(orderModels, agencyId, null);
					if (CollectionUtils.isEmpty(list)) {
						continue;
					}
					yiBaoPaymentService.transfer(list, agencyId, null);
				}
			} catch (Exception e) {
				log.info("代理：{}没有自动放款的订单",agencyId);
			}
		}
	}
}
