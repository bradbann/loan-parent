package org.songbai.loan.service.finance.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.service.finance.dao.FinanceDao;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/15 12:00
 * Description:
 */
@Service
public class ComFinanceServiceImpl implements ComFinanceService {

	@Autowired
	private FinanceDao financeDao;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private Map<String, Object> map = new HashMap<>();

	@Override
	public PlatformConfig getPayPlatformConfig(Integer agencyId) {
		PlatformConfig config = financeDao.getPayPlatformConfig(agencyId, FinanceConstant.Status.ENABLE.key);
		if (config == null) {
			throw new BusinessException(UserRespCode.MERCHANT_NOT_FOUND, "该商户暂不支持交易");
		}
		return config;
	}

	@Override
	public String getPayCodeByAgency(Integer agencyId) {
		PlatformConfig config = getPayPlatformConfig(agencyId);
		String platformCode = financeDao.getCodeByPlatformId(config.getPlatformId());
		if (StringUtils.isBlank(platformCode)) {
			throw new BusinessException(UserRespCode.MERCHANT_NOT_FOUND, "该支付平台未启用");
		}
		return platformCode;
	}

	@Override
	public FinanceBankModel getBankModelByBankCodeAndPlatformId(Integer agencyId, String bankCode) {
		PlatformConfig config = getPayPlatformConfig(agencyId);
		FinanceBankModel model = financeDao.getBankModelByBankCodeAndPlatformId(config.getPlatformId(), bankCode);
		if (model == null) {
			throw new BusinessException(UserRespCode.NOT_SUPPORT_THIS_BANK, "该银行卡暂不支持");
		}
		return model;
	}

	@Override
	public UserBankCardModel getUserDefaultBankCard(Integer userId) {
		UserBankCardModel cardModel = financeDao.getUserDefaultBankCard(userId);
		if (cardModel == null) {
			throw new BusinessException(AdminRespCode.HAS_NOT_DEFAULT_CARD, "用户" + userId + "没有设置默认银行卡");
		}
		return cardModel;
	}

	@Override
	public String getYiBaoSellIdByAgencyId(Integer agencyId) {
		String sellId = (String) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_SELLID, agencyId);
		if (StringUtils.isNotBlank(sellId)) {
			return sellId;
		}
		List<String> params = financeDao.selectConfigByPlatformCode(agencyId, FinanceConstant.PayPlatform.YIBAO.code, null);
		String param = params.get(0);
		PlatformConfig platformConfig = JSON.parseObject(param, PlatformConfig.class);
		String temp = platformConfig.getSellId();
		if (StringUtils.isBlank(sellId)) {
			throw new BusinessException(UserRespCode.MERCHANT_NOT_FOUND, "该支付平台未启用");
		}
		redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_SELLID, agencyId, temp);
		return temp;
	}



	@Override
	public PlatformConfig getPlatformConfig(Integer agencyId ,String payPlatform){

		List<String> params = financeDao.selectConfigByPlatformCode(agencyId, payPlatform, null);

		if(params == null || params.isEmpty()){
			throw new BusinessException(UserRespCode.MERCHANT_NOT_FOUND, "该商户暂不支持交易");
		}

		PlatformConfig platformConfig = JSON.parseObject(params.get(0), PlatformConfig.class);

		return platformConfig;
	}

	@Override
	public Integer getAgencyIdByMD5(String agencyMD5) {
		Integer agencyId = (Integer) map.get(agencyMD5);
		if (agencyId == null) {
			Integer id = financeDao.getAgencyIdByMD5(agencyMD5);
			if (id == null) {
				throw new BusinessException("查询不到agencyMD5：【" + agencyMD5 + "】的代理");
			}
			map.put(agencyMD5, id);
			return id;
		}
		return agencyId;
	}

	@Override
	public String getAgencyMd5ById(Integer agencyId) {
		String agencyMD5 = (String) map.get(agencyId + "");
		if (agencyMD5 == null){
			String md5 = financeDao.getAgencyMD5ById(agencyId + "");
			if (md5 == null){
				throw new BusinessException("查询不到agencyId：【" + agencyId + "】的代理的md5值");
			}
			map.put(agencyId + "",md5);
			return md5;
		}
		return agencyMD5;
	}

}
