package org.songbai.loan.risk.mould.variable;

import org.songbai.loan.risk.vo.VariableMergeVO;

public interface MergeCalc {


    /**
     * @param resultVO
     */
    void merge(VariableMergeVO resultVO);


    void mergeCatalog(String userId, Integer catalog);

}
