package org.songbai.loan.user.finance.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.RepaymentFlowModel;

public interface RepaymentFlowDao extends BaseMapper<RepaymentFlowModel> {

    RepaymentFlowModel findFlowByOrderNumber(@Param("orderNumber") String orderNumber);

}
