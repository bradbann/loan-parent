package org.songbai.loan.risk.service;

import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderVO;

/**
 * @author navy
 */
public interface UserMouldService {


    void calcAsyncMulti(RiskOrderVO vo);

    /**
     * @param vo
     */
    void calcAsync(RiskOrderVO vo);

    RiskResultVO calc(RiskOrderVO vo);
}
