package org.songbai.loan.admin.schdule.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.service.finance.YiBaoSdkCacheConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author: wjl
 * @date: 2018/12/29 16:23
 * Description: 易宝配置更新
 */
@Component
public class YiBaoConfigListener {

	private static final Logger log = LoggerFactory.getLogger(YiBaoConfigListener.class);

	@Autowired
	private YiBaoSdkCacheConfigProvider yiBaoSdkCacheConfigProvider;

	@JmsListener(destination = JmsDest.PAYPLATFORM_CONFIG)
	public void flushCache() {
		yiBaoSdkCacheConfigProvider.flushCache();
		log.info("*****************************************有代理修改了易宝配置，已重新加载配置");
	}
}
