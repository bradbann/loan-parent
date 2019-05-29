package org.songbai.loan.admin.order.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.order.po.OrderOptPo;
import org.songbai.loan.admin.order.vo.OptListVO;
import org.songbai.loan.admin.order.vo.OrderOptPageVo;
import org.songbai.loan.model.loan.OrderOptModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderOptService {
    /**
     * 创建订单流水
     *
     * @param optModel
     */
    void createOrderOpt(OrderOptModel optModel);

    Ret getOwnerRecord(Integer agencyId, Integer actorId);

    Page<OrderOptPageVo> getOrderOptPage(OrderOptPo po);

    /**
     * 查询订单记录
     */
    OrderOptModel findOptLimitOne(String orderNumber, Integer agencyId, Integer stage, Integer status);

    List<OptListVO> findOptList(Integer agencyId, String orderNumber);
}
