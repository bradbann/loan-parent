package org.songbai.loan.user.finance.service;

import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.FinanceDeductFlowModel;
import org.songbai.loan.model.loan.FinanceDeductModel;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;

public interface FinanceDeductService {
    FinanceDeductModel selectDeductModelById(Integer id);

    FinanceDeductFlowModel selectDeductFlowModelByDeductId(Integer deductId);

    FinanceDeductFlowModel saveDeductFlow(FinanceDeductModel deductModel, PayOrderVO orderVO, PayBankCardVO payBankCardVO);

    void updateDeductFlowForPayResult(FinanceDeductFlowModel flowModel, PayResultVO resultVO);

    void updateDeductStatus(FinanceDeductModel deductModel, Integer sts, String msg);

    Integer queryDeductSuccessCount(Integer deductId);

    void updateDeductStatusAndNum(FinanceDeductModel deductModel, Integer sts, String msg);

    FinanceDeductModel updateDeductMoney(Integer deductId, Double money);

    FinanceDeductFlowModel updateDeductFlowStatusForIoModel(FinanceIOModel ioModel, boolean success, String msg);

    double[] getDeductLimit(FinanceDeductModel deductModel,FinanceDeductFlowModel lastDeductFlow);
}
