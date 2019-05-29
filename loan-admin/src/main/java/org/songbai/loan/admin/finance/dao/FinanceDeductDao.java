package org.songbai.loan.admin.finance.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.finance.model.po.DeductPo;
import org.songbai.loan.admin.finance.model.po.DeductQueuePO;
import org.songbai.loan.admin.finance.model.vo.DeductPageVo;
import org.songbai.loan.admin.finance.model.vo.DeductQueueVo;
import org.songbai.loan.model.loan.FinanceDeductModel;

import java.util.List;
import java.util.Map;

public interface FinanceDeductDao extends BaseMapper<FinanceDeductModel> {
    Integer findDeductFlowCount(@Param("po") DeductPo po);

    List<DeductPageVo> findDeductFlowList(@Param("po") DeductPo po);

    Map findDeductTotal(@Param("agencyId") Integer agencyId);

    int findDeductQueueCount(@Param("po") DeductQueuePO po);

    List<DeductQueueVo> findDeductQueueList(@Param("po") DeductQueuePO po);

}
