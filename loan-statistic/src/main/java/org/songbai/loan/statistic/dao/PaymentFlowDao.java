package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.PaymentFlowModel;
import org.songbai.loan.model.statistic.PayStatisticModel;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 8:16 PM
 */
public interface PaymentFlowDao extends BaseMapper<PaymentFlowModel> {

    /**
     * 查询代理昨日放款统计
     * @param agencyId
     * @param yesterday
     * @return
     */
    PayStatisticModel findAgencyPaymentStatisticByYesterday(@Param("agencyId") Integer agencyId, @Param("yesterday") Date yesterday);

    /**
     *
     * @param type 1首借 2复借
     * @return
     */
    PayStatisticModel findAgencyPaymentStatisticLoanByYesterday(@Param("type") int type, @Param("agencyId") Integer agencyId, @Param("yesterday") Date yesterday);

}
