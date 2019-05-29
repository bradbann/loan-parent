package org.songbai.loan.admin.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.vo.ActorReviewVo;
import org.songbai.loan.admin.statistic.model.vo.OrderReviewVo;
import org.songbai.loan.model.statistic.ReviewOrderModel;

import java.util.List;
import java.util.Map;

public interface ReviewOrderDao extends BaseMapper<ReviewOrderModel> {

    Integer getAgencyReviewCount(@Param("po") ReviewStatisPo po);

    Integer getActorStatisCount(@Param("po") ReviewStatisPo po);

    /**
     * 信审人员汇总统计
     */
    List<ActorReviewVo> findActorStatisGroupList(@Param("po") ReviewStatisPo po);

    /**
     * 信审人员汇总统计
     */
    List<OrderReviewVo> findReviewGroupList(@Param("po") ReviewStatisPo po);

    Map<String, Object> queryOrderTotalSum(@Param("agencyId") Integer agencyId);
}
