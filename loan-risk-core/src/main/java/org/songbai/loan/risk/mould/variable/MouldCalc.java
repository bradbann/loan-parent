package org.songbai.loan.risk.mould.variable;

import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderVO;

public interface MouldCalc {


    /**
     * @param riskOrderVO
     * @return
     */
    public RiskResultVO calc(RiskOrderVO riskOrderVO);


}
