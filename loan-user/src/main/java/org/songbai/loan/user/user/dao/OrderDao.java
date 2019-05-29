package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.user.user.model.vo.OrderListVO;
import org.songbai.loan.user.user.model.vo.OrderVO;

import java.util.List;

public interface OrderDao extends BaseMapper<OrderModel> {
    /**
     * 查找用户最近的一笔订单
     *
     * @return
     */
    OrderModel finRecentOrderByUserId(Integer userId);

    int selectOrderCount(Integer userId);

    List<OrderListVO> selectOrderList(@Param("userId") Integer userId, @Param("page") PageRow pageRow);

    OrderModel selectOrderByOrderNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Integer userId);

    /**
     * 查询用户还款成功次数
     */
    int findUserLoanSuccessLoanOrder(Integer userId);

    /**
     * 查找用户最近完成的一笔订单
     *
     * @return
     */
    OrderModel finRecentCompleteOrderByUserId(Integer userId);

    /**
     * 查找待还款的所有订单
     */
    List<OrderVO> selectWaitRepaymentOrderToday(@Param("ids") String ids, @Param("agencyId") Integer agencyId);

    /**
     * 用户自动扣款的金额修改。
     *
     * @param id
     * @param money
     */
    void updateOrderForDeduct(@Param("orderId") Integer id, @Param("deductMoney") Double money);

    void updateOrderStatus(@Param("stage") Integer stage, @Param("status") Integer status,
                           @Param("orderNum") String orderNum);

    /**
     * 查询最后一条待还款订单
     */
    OrderModel findWaitRepaymentOrder(@Param("userId") Integer userId);
}
