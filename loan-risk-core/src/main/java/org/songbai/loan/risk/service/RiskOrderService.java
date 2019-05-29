package org.songbai.loan.risk.service;

import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.risk.vo.RiskResultVO;

import java.util.List;

public interface RiskOrderService {
    void submitOrder(String userId, String orderNumber);

    UserRiskOrderModel selectRiskOrderModel(String userId, String orderNumber);

    void authFinish(RiskResultVO resultVO);

    void waitData(RiskResultVO resultVO);

    void authFail(RiskResultVO resultVO);

    List<UserRiskOrderModel> selectUserRiskOrderModel();
}
