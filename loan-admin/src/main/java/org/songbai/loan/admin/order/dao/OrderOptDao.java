package org.songbai.loan.admin.order.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.order.po.OrderOptPo;
import org.songbai.loan.admin.order.vo.OrderOptPageVo;
import org.songbai.loan.admin.order.vo.OrderOptVo;
import org.songbai.loan.model.loan.OrderOptModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderOptDao extends BaseMapper<OrderOptModel> {

    List<OrderOptVo> findOwnerReviewList(@Param("agencyId") Integer agencyId, @Param("actorId") Integer actorId,
                                         @Param("date") Date date);

    Integer queryOrderOptCount(@Param("po") OrderOptPo po);

    List<OrderOptPageVo> queryOrderOptPage(@Param("po") OrderOptPo po);

    Map<String, Object> queryAgencyGroupReviewOrder(@Param("agencyId") Integer agencyId);

    OrderOptModel getLastUpdateOpt(@Param("orderNum") String orderNum, @Param("userId") Integer userId);

    OrderOptModel findOptLimitOne(@Param("orderNum") String orderNumber, @Param("agencyId") Integer agencyId,
                                  @Param("stage") Integer stage, @Param("status") Integer status);

    List<OrderOptModel> findOptListByOrderNumber(@Param("orderNumber") String orderNumber);

}
