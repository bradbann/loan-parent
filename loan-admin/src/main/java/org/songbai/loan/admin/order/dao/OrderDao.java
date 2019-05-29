package org.songbai.loan.admin.order.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.order.po.OrderPaymentPO;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.po.RepayListPO;
import org.songbai.loan.admin.order.vo.OrderMachineVo;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.songbai.loan.admin.order.vo.OrderPaymentVO;
import org.songbai.loan.admin.order.vo.OrderRepayVO;
import org.songbai.loan.admin.statistic.model.vo.StatisHomeVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.OrderModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderDao extends BaseMapper<OrderModel> {


    Integer queryOrderCount(@Param("po") OrderPo orderPo);

    List<OrderPageVo> queryOrderPage(@Param("po") OrderPo orderPo);

    /**
     * 修改订单为已取单
     */
    Integer updateOrderActorId(@Param("agencyId") Integer agencyId, @Param("limit") Integer limit,
                               @Param("actorId") Integer actorId, @Param("date") Date date);

    List<OrderPageVo> getOwnerReviceOrder(@Param("po") OrderPo orderPo, @Param("agencyId") Integer agencyId,
                                          @Param("actorId") Integer actorId);


    void updateOrderStatus(@Param("orderId") Integer orderId, @Param("orderStage") Integer stage,
                           @Param("orderStatus") Integer orderStatus, @Param("operator") Integer operator,
                           @Param("operaDate") Date operaDate);

    List<OrderModel> findOrderListByReview(@Param("orderNumbers") List<String> orderNumbers,
                                           @Param("agencyId") Integer agencyId);

    void returnOrderById(@Param("orderId") Integer orderId);

    int findPaymentCount(@Param("po") OrderPaymentPO po);

    List<OrderPaymentVO> findPaymentList(@Param("po") OrderPaymentPO po, @Param("page") PageRow pageRow);


    //以下为用户详情页统计所用

    /**
     * 查询用户所有的单子
     */
    Integer getOrderCountByUserId(@Param("userIds") String userIds, @Param("map") Map<String, Integer> map);

    void updateOrderPayment(OrderModel update);

    int findRepayOrderCount(@Param("po") RepayListPO po);

    List<OrderRepayVO> findRepayOrderCountList(@Param("po") RepayListPO po, @Param("page") PageRow pageRow);

    /**
     * 查询逾期订单
     */
    List<OrderModel> queryChaseOrderList(@Param("date") LocalDate date, @Param("limit") Integer limit, @Param("pageSize") int pageSize);


    OrderModel selectInfoByOrderNumb(@Param("orderNumber") String orderNumber);

    List<OrderModel> findOrderListByOrderNumbs(@Param("orderNumbers") String[] orderNumbers);

    void updateOrderDeductMoney(@Param("id") Integer id, @Param("deductMoney") Double deductMoney);


    OrderModel selectOrderByOrderNumberAndAgencyId(@Param("orderNumber") String orderNumber, @Param("agencyId") Integer agencyId);

    void updateOrderOverdue(@Param("id") Integer id, @Param("exceedFee") Double exceedFee, @Param("orderStatus") Integer orderStatus);

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
     * 查询还款用户id
     */
    List<Integer> findTodayRepayOrder(@Param("today") LocalDate today, @Param("agencyId") Integer agencyId);

    void updateOrderChaseInfoById(@Param("id") Integer id, @Param("chaseDeptId") Integer chaseDeptId,
                                  @Param("chaseId") String chaseId, @Param("chaseActorId") Integer chaseActorId,
                                  @Param("chaseDate") Date chaseDate);

    /**
     * 查询该代理下的逾期订单
     */
    List<OrderModel> findOrderOverdueByAgencyId(Integer agencyId);

    /**
     * 查询代理的总订单、待复审订单
     */
    Map<String, Object> queryAgencyGroupCount(@Param("agencyId") Integer agencyId);

    StatisHomeVO findStatisticOrderByAgencyIdAndDate(@Param("agencyId") Integer agencyId, @Param("date") String date);

    /**
     * 机审失败订单数量
     */
    Integer getMachineFailCount(@Param("po") OrderPo po);

    /**
     * 获取机审失败list
     */
    List<OrderMachineVo> findMachineFailList(@Param("po") OrderPo po);

    /**
     * 查询所有待放款订单
     */
    List<OrderModel> selectWaitTransferOrderByAgencyId(Integer agencyId);

    List<OrderModel> selectOrderByIdsAndAgencyId(@Param("orderNumbers") List<String> orderNumbers,@Param("agencyId") Integer agencyId);
    //查询共债记录
    Map queryCommonDebt(@Param("agencyId") Integer agencyId, @Param("userIds") List<Integer> userIds);

    List<OrderPageVo> findUserOrderHistList(@Param("agencyId") Integer agencyId, @Param("userIds") List<Integer> userIds);

    void updateOrderDeductMoneyComplate(@Param("id") Integer id, @Param("deductMoney") Double deductMoney);

    List<OrderModel> findOrderListByRepayMentDate(@Param("agencyId") Integer agencyId,@Param("repaymentDate") String repaymentDate);

    OrderModel finRecentOrderByUserId(Integer userId);
}
