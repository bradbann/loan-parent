package org.songbai.loan.risk.service.statis.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.OrderModel;

public interface RiskOrderDao extends BaseMapper<OrderModel> {


    /**
     * 查询用户最近的一笔订单
     */
    OrderModel findRecentOrderByUserId(Integer userId);

    /**
     * 查询用户逾期次数
     */
    int findOrderOverdueCountByUserId(Integer userId);

    /**
     * 查询用户逾期x天次数
     */
    int findOrderOverdueCountByUserIdAndDays(@Param("userId") Integer userId, @Param("days") Integer days);

    /**
     * 人工复审拒绝次数
     *
     * @param userId
     * @return
     */
    int findOrderCustomRefuseCount(Integer userId);

    /**
     * 查询用户复借成功的次数
     * @param userId
     * @return
     */
    int selectOrderSuccessCount(@Param("userId") Integer userId);
}
