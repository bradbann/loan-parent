package org.songbai.loan.admin.statistic.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.songbai.loan.admin.statistic.model.vo.*;
import org.songbai.loan.common.util.PageRow;

/**
 * Author: qmw
 * Date: 2018/11/21 4:35 PM
 */
public interface StatisticService {
    StatisHomeVO statisticHome(Integer agencyId, String date);

    Ret reviewTotalStatis(Integer agencyId);

    Page<OrderReviewVo> getAgencyReviewPage(ReviewStatisPo po);

    Page<ActorReviewVo> getActorReviewStatisPage(ReviewStatisPo po);


    Page<StatisticPayVO> statisticPayment(StatisticPayPO po, PageRow pageRow);

    Page<UserStatisVo> getChannelStatisPage(ChannelStatisPo po);

    Page<StatisticRepayVO> statisticRepayment(StatisticPayPO po, PageRow pageRow);

    Page<StatisticUserVO> statisticUser(StatisticUserPO po, PageRow pageRow);

    Page<StatisticUserVO> statisticActionUser(StatisticUserPO po, PageRow pageRow);

}
