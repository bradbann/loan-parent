package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.statistic.RepayStatisticModel;
import org.songbai.loan.model.statistic.UserStatisticModel;
import org.songbai.loan.statistic.model.po.OrderPO;
import org.songbai.loan.statistic.model.po.PushConditionPO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderDao extends BaseMapper<OrderModel> {



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
    List<Integer> findTodayRepayOrder(@Param("today") LocalDate today, @Param("vestId") Integer vestId,@Param("page") PageRow page);

    void updateOrderChaseInfoById(@Param("id") Integer id, @Param("chaseDeptId") Integer chaseDeptId,
                                  @Param("chaseId") String chaseId, @Param("chaseActorId") Integer chaseActorId,
                                  @Param("chaseDate") Date chaseDate);
    /**
     * 查询该代理下的逾期订单
     * @return
     */
    List<OrderPO> findOrderOverdueByAgencyId(@Param("vestId") Integer vestId);

    /**
     * 根据创建日期分组查询待审核订单量
     */
    List<Map<String, Integer>> queryWaitReviewOrderGroup(@Param("createTime") String createTime);

    List<RepayStatisticModel> findTodayRepayStatisticByAgencyId(@Param("agencyId") Integer agencyId, @Param("repayDate") Date repayDate);

    /**
     * 查询今日提单数
     */
    UserStatisticModel findOrderCountByAgencyId(@Param("agencyId") Integer agencyId,  @Param("date") String date);

    List<Integer> findTodayRepayOrderAgencyId(@Param("today") LocalDate today);

    List<PushConditionPO> findOrderOverdueDaysAndFee();


}
