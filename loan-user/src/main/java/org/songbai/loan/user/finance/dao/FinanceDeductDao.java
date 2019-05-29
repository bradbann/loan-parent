package org.songbai.loan.user.finance.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.FinanceDeductFlowModel;
import org.songbai.loan.model.loan.FinanceDeductModel;

import java.util.List;

public interface FinanceDeductDao extends BaseMapper<FinanceDeductModel> {


    public List<FinanceDeductFlowModel> selectFinanceDeductFlowByDeductId(@Param("deductId") Integer deductId);


    public FinanceDeductFlowModel selectLastFinanceDeductFlowByDeductId(@Param("deductId") Integer deductId);


    void updateDeductMoney(@Param("id") Integer id, @Param("money") Double money);

}


