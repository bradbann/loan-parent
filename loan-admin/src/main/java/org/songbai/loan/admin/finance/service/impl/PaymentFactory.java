package org.songbai.loan.admin.finance.service.impl;

import org.songbai.loan.admin.finance.service.PaymentService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2019/1/2 15:19
 * Description:
 */
@Component
public class PaymentFactory implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, PaymentService> map = new HashMap<>();

	@PostConstruct
	public void init() {
		String[] names = applicationContext.getBeanNamesForType(PaymentService.class);
		for (String name : names) {
			PaymentService bean = (PaymentService) applicationContext.getBean(name);
			map.put(bean.getCode(), bean);
		}
	}

	public PaymentService getBeanByCode(String code) {
		return map.get(code);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
