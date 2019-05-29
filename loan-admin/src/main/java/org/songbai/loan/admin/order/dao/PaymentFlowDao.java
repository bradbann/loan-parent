package org.songbai.loan.admin.order.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.order.po.PaymentRecordPO;
import org.songbai.loan.admin.order.vo.OrderPayRecordVO;
import org.songbai.loan.admin.order.vo.PaymentStatisticsVO;
import org.songbai.loan.admin.statistic.model.vo.StatisHomeVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.PaymentFlowModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/7 8:16 PM
 */
public interface PaymentFlowDao extends BaseMapper<PaymentFlowModel> {
    int findPaymentRecordCount(@Param("po") PaymentRecordPO po);

    List<OrderPayRecordVO> findPaymentRecordList(@Param("po") PaymentRecordPO po, @Param("page") PageRow pageRow);

    PaymentStatisticsVO findPaymentCountByDate(@Param("agencyId") Integer agencyId, @Param("actorId") Integer actorId, @Param("minTime") LocalDateTime minTime, @Param("maxTime") LocalDateTime maxTime);


    StatisHomeVO findStatisticOrderByAgencyIdAndDate(@Param("agencyId") Integer agencyId, @Param("date") String date);

}
