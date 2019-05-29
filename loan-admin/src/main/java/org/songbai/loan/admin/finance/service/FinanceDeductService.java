package org.songbai.loan.admin.finance.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.finance.model.po.DeductPo;
import org.songbai.loan.admin.finance.model.po.DeductQueuePO;
import org.songbai.loan.admin.finance.model.vo.DeductPageVo;
import org.songbai.loan.admin.finance.model.vo.DeductQueueVo;

import java.util.List;
import java.util.Map;

public interface FinanceDeductService {


    List<String> saveFinanceDeductModel(List<String> orderNumbers, Integer agencyId, AdminUserModel actorModel);

    Page<DeductPageVo> findDeductFlowList(DeductPo po);

    Map findDeductTotal(Integer agencyId);

    Page<DeductQueueVo> findDeductQueue(DeductQueuePO po);

    void cancelRepay(String orderNumber, Integer agencyId);

}
