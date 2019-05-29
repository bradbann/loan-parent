package org.songbai.loan.service.finance.service;

import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.user.UserBankCardModel;

public interface ComFinanceService {

	/**
	 * 查询当前平台启用的支付通道的配置
	 */
	PlatformConfig getPayPlatformConfig(Integer agencyId);

	/**
	 * 根据当前平台启用的支付通道，返回支付平台code
	 */
	String getPayCodeByAgency(Integer agencyId);

	/**
	 * 根据平台银行卡code拿到第三方平台银行卡code
	 */
	FinanceBankModel getBankModelByBankCodeAndPlatformId(Integer agencyId, String bankCode);

	/**
	 * 查询用户默认的银行卡
	 */
	UserBankCardModel getUserDefaultBankCard(Integer userId);

	/**
	 * 根据agencyId拿到代理商的易宝支付商户号
	 */
	String getYiBaoSellIdByAgencyId(Integer agencyId);

	PlatformConfig getPlatformConfig(Integer agencyId, String payPlatform);

	/**
	 * 根据agencyMD5查询代理agencyId
	 */
	Integer getAgencyIdByMD5(String agencyMD5);

	/**
	 * 根据agencyId查询代理agencyMD5
	 */
	String getAgencyMd5ById(Integer agencyId);

}
