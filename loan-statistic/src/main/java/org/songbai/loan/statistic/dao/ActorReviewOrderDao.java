package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.statistic.ActorReviewOrderModel;

import java.time.LocalDate;

public interface ActorReviewOrderDao extends BaseMapper<ActorReviewOrderModel> {


    ActorReviewOrderModel getInfoByAgencyId(@Param("agencyId") Integer agencyId, @Param("calcDate") LocalDate calcDate,
                                            @Param("actorId") Integer actorId, @Param("vestId") Integer vestId);


    Integer updateActorReviewInfo(@Param("model") ActorReviewOrderModel model);
}
