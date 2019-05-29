package org.songbai.loan.admin.chase.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.chase.po.ChaseDebtPo;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public interface ChaseService {

    /**
     * 催收分配列表
     */
    Page<OrderPageVo> getChasePage(ChaseDebtPo po);

    /**
     * 分单
     */
    void seperateOrder(String orderNumbers, Integer deptId, Integer actorId);

    /**
     * 坏账
     */
    void doBadDebt(String orderNumber, Integer agencyId, Integer actorId);

    void exportChasePage(ChaseDebtPo po, HttpServletResponse response);

    /**
     * 组内分配--分页
     */
    Page<OrderPageVo> getGroupChasePage(ChaseDebtPo po, AdminUserModel userModel);

    List<AdminUserModel> getChaseDeptActor(AdminUserModel userModel, Integer deptType);

    /**
     * 组内分单
     */
    void groupSeperateOrder(String orderNumbers, Integer actorId, Integer currentActorId);

    /**
     * 我的催单
     */
    Page<OrderPageVo> getOwnerChasePage(ChaseDebtPo po);

    void exportOwnerChasePage(ChaseDebtPo po, HttpServletResponse response);
}
