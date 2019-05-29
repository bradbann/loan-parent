package org.songbai.loan.risk.mould.variable.over;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.risk.mould.variable.MouldCalc;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MouldCalcImplTest {

    @Autowired
    MouldCalc mouldCalc;

    @Test
    public void calc() {


        RiskResultVO resultVO  = mouldCalc.calc(RiskOrderVO.builder().thridId("1111").orderNumber("11111111").build());


        System.out.println(resultVO);
    }
}