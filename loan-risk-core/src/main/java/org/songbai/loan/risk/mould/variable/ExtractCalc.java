package org.songbai.loan.risk.mould.variable;

import org.songbai.loan.risk.vo.VariableMergeVO;
import org.songbai.loan.risk.vo.VariableExtractVO;

public interface ExtractCalc {

    String source();

    VariableMergeVO calc(VariableExtractVO calcVO);
}
