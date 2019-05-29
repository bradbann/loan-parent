package org.songbai.loan.admin.chase.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.chase.po.ChaseDebtPo;
import org.songbai.loan.admin.chase.vo.ChaseExcelVo;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.songbai.loan.model.loan.OrderModel;

import java.util.List;
import java.util.Set;

public interface ChaseDebtDao {

    Integer queryChaseCount(@Param("po") ChaseDebtPo po);

    List<OrderPageVo> queryChasePageList(@Param("po") ChaseDebtPo po);

    /**
     * 将订单置为坏账
     */
    void doBadDebt(@Param("id") Integer id, @Param("orderStatus") Integer orderStatus);

    /**
     * 根据催收号获取订单
     */
    OrderModel getOrderByChaseId(@Param("chaseId") String chaseId);

    List<ChaseExcelVo> queryChaseExcelList(@Param("po") ChaseDebtPo po);

    Integer getGroupChaseCount(@Param("po") ChaseDebtPo po, @Param("deptIds") List<Integer> deptIds);

    List<OrderPageVo> findGroupChaseList(@Param("po") ChaseDebtPo po, @Param("deptIds") List<Integer> deptIds);

    Integer queryOwnerChaseCount(@Param("po") ChaseDebtPo po);

    List<OrderPageVo> queryOwnerChasePage(@Param("po") ChaseDebtPo po);

    /**
     * 我的催单excel导出
     */
    List<ChaseExcelVo> queryOwnerChaseExcelList(@Param("po") ChaseDebtPo po);
}
