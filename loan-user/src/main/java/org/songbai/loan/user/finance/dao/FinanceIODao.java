package org.songbai.loan.user.finance.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.finance.FinanceIOModel;

import java.time.LocalDateTime;
import java.util.List;

public interface FinanceIODao extends BaseMapper<FinanceIOModel> {

    FinanceIOModel getModelByUserIdOrderIdRequestId(@Param("thirdId") String thirdId, @Param("orderNum") String orderNum, @Param("requestId") String requestId);

    List<Integer> selectRepayProcessingOrder(String code);

    FinanceIOModel findFinanceIoByOrderNumber(String orderNumber);

    FinanceIOModel getLastModelByUserIdOrderId(@Param("orderId") String orderId, @Param("userId") Integer userId);

    /**
     * 查询聚合未处理订单,查询2-60分钟的订单
     */
    List<FinanceIOModel> selectNotConfirmOrderByJh(@Param("page") Integer page, @Param("pageSize") Integer pageSize,
                                                   @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    FinanceIOModel getIoModelByOrderIdAndRequestId(@Param("orderNumber") String orderNumber, @Param("requestId") String requestId,
                                                   @Param("userId") Integer userId);
}
