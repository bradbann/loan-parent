package org.songbai.loan.admin.risk.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.risk.model.UserRiskOrderVO;
import org.songbai.loan.admin.risk.model.po.RiskOrderPO;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;

import java.util.List;

public interface UserRiskOrderService {
    Page<UserRiskOrderVO> selectRiskOrderList(RiskOrderPO po);

    List<RiskUserMouldCatalogModel> selectMouldCatalog(String userId, String orderNumber);
}
