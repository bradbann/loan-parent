package org.songbai.loan.admin.order.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.vo.OrderMachineVo;
import org.springframework.stereotype.Component;

@Component
public interface OrderMachineService {
    Page<OrderMachineVo> findMachineFailPage(OrderPo po);

    void updateMachineOrderStatus(String orderNumber, Integer agencyId, Integer actorId);
}
