package org.songbai.loan.service.finance.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.user.UserBankCardModel;

import java.util.List;

public interface FinanceDao {

	PlatformConfig getPayPlatformConfig(@Param("agencyId") Integer agencyId, @Param("status") Integer status);

	String getCodeByPlatformId(@Param("id") Integer id);

	FinanceBankModel getBankModelByBankCodeAndPlatformId(@Param("platformId") Integer platformId, @Param("bankCode") String bankCode);

	UserBankCardModel getUserDefaultBankCard(@Param("userId") Integer userId);

	//根据agenceId、platformCode、status去查param
	List<String> selectConfigByPlatformCode(@Param("agencyId") Integer agencyId, @Param("platformCode") String platformCode, @Param("status") Integer status);

	List<String> selectYibaoConfigUpForAllAgency();

	Integer getAgencyIdByMD5(String agencyMD5);

	String getAgencyMD5ById(String id);

}
