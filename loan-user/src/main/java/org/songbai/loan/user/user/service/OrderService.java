package org.songbai.loan.user.user.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.user.user.model.vo.LoanDetail;
import org.songbai.loan.user.user.model.vo.OrderListVO;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: qmw
 * Date: 2018/10/31 4:23 PM
 */
public interface OrderService {
    void loan(Integer userId, Double loan);

    Page<OrderListVO> orderList(Integer userId, PageRow pageRow);

    Ret orderDetailByOrderNumber(Integer userId, String orderNumber);

    LoanDetail loanDetail(Integer userId, Integer agencyId, Double loan);

    Ret loanHome(Integer userId, HttpServletRequest request);

}
