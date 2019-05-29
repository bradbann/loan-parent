package org.songbai.loan.user.finance.service.impl;

import org.songbai.loan.user.finance.service.RepaymentService;
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
 * @date: 2019/1/3 18:23
 * Description: 还款工厂
 */
@Component
public class RepaymentFactory implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, RepaymentService> map = new HashMap<>();

	@PostConstruct
	public void init(){
		String[] names = applicationContext.getBeanNamesForType(RepaymentService.class);
		for (String name : names) {
			RepaymentService bean = (RepaymentService) applicationContext.getBean(name);
			map.put(bean.getCode(),bean);
		}
	}

	public RepaymentService getBeanByCode(String code){
		return map.get(code);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
