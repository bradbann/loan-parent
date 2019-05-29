package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.statistic.ReviewOrderModel;

import java.time.LocalDate;

public interface ReviewOrderDao extends BaseMapper<ReviewOrderModel> {
    /**
     * 更新信审统计
     */
    Integer updateReviewOrder(@Param("model") ReviewOrderModel model);

    ReviewOrderModel getInfoBy(@Param("agencyId") Integer agencyId, @Param("calcDate") LocalDate calcDate,
                               @Param("vestId") Integer vestId, @Param("channelCode") String channelCode,
                               @Param("productId") Integer productId, @Param("productGroupId") Integer productGroupId);
}
