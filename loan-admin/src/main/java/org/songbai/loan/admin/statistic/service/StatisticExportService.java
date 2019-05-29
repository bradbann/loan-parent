package org.songbai.loan.admin.statistic.service;

import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public interface StatisticExportService {

    void exportReviewStatis(ReviewStatisPo po, HttpServletResponse response);

    void exportActorReviewStatis(ReviewStatisPo po, HttpServletResponse response);

    void exportChannelStatis(ChannelStatisPo po, HttpServletResponse response);

    void statisticRepaymentStatis(StatisticPayPO po, Integer currentAgencyId, HttpServletResponse response);

    void statisticPaymentStatis(StatisticPayPO po, Integer agencyId, HttpServletResponse response);

    void statisticUserStatis(StatisticUserPO po, Integer agencyId, HttpServletResponse response);

    void statisticActionUserStatis(StatisticUserPO po, Integer agencyId, HttpServletResponse response);

}
