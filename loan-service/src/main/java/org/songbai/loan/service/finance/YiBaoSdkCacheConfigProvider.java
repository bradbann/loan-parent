package org.songbai.loan.service.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yeepay.g3.sdk.yop.config.*;
import com.yeepay.g3.sdk.yop.encrypt.CertTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.service.finance.dao.FinanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/24 21:10
 * Description: 易宝配置注册中心
 */
@Component
public class YiBaoSdkCacheConfigProvider implements AppSdkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(YiBaoSdkCacheConfigProvider.class);

	@Autowired
	private FinanceDao financeDao;

	private Map<String, SDKConfig> cached = new HashMap<>();

	@PostConstruct
	public void init() {
		log.info("*****************************************进入易宝配置注册中心");
		//从数据库读取配置
		List<String> params = financeDao.selectYibaoConfigUpForAllAgency();
		if (CollectionUtils.isEmpty(params)) {
			log.info("*****************************************当前没有代理开启易宝支付");
			return;
		} else {
			log.info("*****************************************当前开启易宝支付的代理有{}个", params.size());
		}
		for (String param : params) {
			for (int i = 0; i < 2; i++) {
				JSONObject jsonObject = JSON.parseObject(param);
				SDKConfig config = new SDKConfig();

				CertConfig publicCertConfig = new CertConfig();
				publicCertConfig.setCertType(CertTypeEnum.RSA2048);
				publicCertConfig.setStoreType(CertStoreType.STRING);
				publicCertConfig.setValue(jsonObject.getString("publicKey"));
				config.setYopPublicKey(new CertConfig[]{publicCertConfig});

				CertConfig privateCertConfig = new CertConfig();
				privateCertConfig.setCertType(CertTypeEnum.RSA2048);
				privateCertConfig.setStoreType(CertStoreType.STRING);
				privateCertConfig.setValue(jsonObject.getString("privateKey"));
				config.setIsvPrivateKey(new CertConfig[]{privateCertConfig});

				config.setServerRoot(jsonObject.getString("url"));
				if (i == 0) {
					config.setAppKey(jsonObject.getString("sellId"));
				} else {
					config.setAppKey(jsonObject.getString("sellId").replace("SQKK", "OPR:"));
				}
				cached.put(config.getAppKey(), config);
			}
		}
		log.info("*****************************************当前注册中心加载了以下appKey:{}", cached.keySet());
		AppSdkConfigProviderRegistry.registerCustomProvider(this);
	}


	public void flushCache() {
		cached.clear();
		init();
	}

	@Override
	public AppSdkConfig getConfig(String key) {
		return AppSdkConfig.Builder.anAppSdkConfig().withSDKConfig(loadSDKConfig(key)).build();
	}

	@Override
	public AppSdkConfig getDefaultConfig() {
		return null;
	}

	@Override
	public AppSdkConfig getConfigWithDefault(String key) {
		return AppSdkConfig.Builder.anAppSdkConfig().withSDKConfig(loadSDKConfig(key)).build();
	}

	private SDKConfig loadSDKConfig(String key) {
		return cached.get(key);
	}


}
