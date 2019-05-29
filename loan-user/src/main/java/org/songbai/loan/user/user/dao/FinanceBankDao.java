package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.finance.FinanceBankModel;

import java.util.List;

public interface FinanceBankDao extends BaseMapper<FinanceBankModel> {

	String selectThirdBankCodeByCodeAndPlatformId(@Param("bankCode") String bankCode, @Param("payPlatformId") Integer payPlatformId);

	List<FinanceBankModel> selectAll();
}
