package org.songbai.loan.admin.order.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.order.po.*;
import org.songbai.loan.admin.order.vo.*;
import org.songbai.loan.common.util.PageRow;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Component
public interface OrderService {

    /**
     * 订单列表
     */
    Page<OrderPageVo> orderList( OrderPo orderPo);

    /**
     * 待复审订单
     */
    Page<OrderPageVo> getWaitReviceOrderPage( OrderPo orderPo);

    /**
     * 获取订单
     */
    Integer takeOrder(Integer count, Integer agencyId, Integer actorId);

    /**
     * 获取自己待审核的订单
     */
    List<OrderPageVo> getOwnerReviceOrder(OrderPo po, Integer agencyId, Integer actorId);

    void updateOrderAuthStatus(String orderNumber, Integer agencyId, Integer actorId, Integer orderStauts, String remark);

    Ret returnReviewOrder(Integer actorId, Integer agencyId, String orderNumber, Integer opeartor);

    /**
     * 待放款列表
     */
    Page<OrderPaymentVO> paymentList(PageRow pageRow, OrderPaymentPO po);

    /**
     * 拒绝放款
     */
    void rejectPay(List<String> ids, Integer agencyId, Integer actorId, String remark, Date againDate);

    /**
     * 放款记录
     */
    Page<OrderPayRecordVO> paymentRecordList(PageRow pageRow, PaymentRecordPO po);

    /**
     * 还款列表
     */
    Page<OrderRepayVO> repayList(PageRow pageRow, RepayListPO po);

    /**
     * 还款确认
     */
    void repayConfirm(RepayPO po);

    Page<OrderRepayRecordVO> repaymentRecordList(PageRow pageRow, RepaymentRecordPO po);

    /**
     * fang
     */
    PaymentStatisticsVO paymentStatistics(Integer agencyId, Integer actorId);

    /**
     * 减免金额
     */
    void repayDeduct(String orderNumber, Double deductMoney, Integer agencyId, Integer actorId,String remark);

    /**
     * 退回
     */
    void paymentReturn(String orderNumber, String remark, Integer actorId, Integer agencyId);

    void exportOrderList(OrderPo po, HttpServletResponse response);
}
