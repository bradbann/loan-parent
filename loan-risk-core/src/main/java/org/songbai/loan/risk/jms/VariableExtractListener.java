package org.songbai.loan.risk.jms;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.constant.risk.RiskJmsDest;
import org.songbai.loan.risk.mould.variable.ExtractCalcFactory;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VariableExtractListener {

    @Autowired
    private ExtractCalcFactory extractCalcFactory;


    /**
     * 变量合并
     */
    @JmsListener(destination = RiskJmsDest.VARIABLE_EXTRACT)
    public void extract(VariableExtractVO calcVO) {


        log.info("receive msg for {} , and start extract variable ", calcVO);

        try {
            extractCalcFactory.extractAndMerge(calcVO);
        } catch (Exception e) {
            log.error("extract variable error ", e);
        }

        log.info("handle msg: {} , and end extract variable", calcVO);
    }


}


