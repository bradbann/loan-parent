package org.songbai.loan.risk.mould.variable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtractCalcFactoryTest {


    @Autowired
    ExtractCalcFactory factory;

    @Test
    public void extractForTaobao() {

        VariableExtractVO calcVO = new VariableExtractVO();

        calcVO.setSources("mx_taobao_report");
        calcVO.setTaskId("3ce1547a-de45-11e8-945d-00163e0c310d");

        calcVO.setUserId("1111");

        factory.extractAndMerge(calcVO);
    }


    @Test
    public void extractForCarrier() {

        VariableExtractVO calcVO = new VariableExtractVO();

        calcVO.setSources("mx_carrier_report");
        calcVO.setTaskId("798f49e0-de4a-11e8-a6e0-00163e13f173");

        calcVO.setUserId("1111");

        factory.extractAndMerge(calcVO);
    }


    @Test
    public void extractForMoxie() {

        VariableExtractVO calcVO = new VariableExtractVO();

        calcVO.setSources(VariableConst.VAR_SOURCE_MOXIE_REPORT);

        calcVO.setUserId("1111");

        factory.extractAndMerge(calcVO);
    }


    @Test
    public void extractForPlatformContacts() {

        VariableExtractVO vo1 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_PLATFORM_CONTACTS)
                .orderNumber("11541145967150486").userId("6a0a8dc16a9444729ebb9dcae0b78497").build();

        factory.extractAndMerge(vo1);
    }


    @Test
    public void extractForPlatformBase() {
        VariableExtractVO vo2 = VariableExtractVO.builder()
                .sources(VariableConst.VAR_SOURCE_PLATFORM_BASE)
                .orderNumber("11541145967150486").userId("6a0a8dc16a9444729ebb9dcae0b78497").build();

        factory.extractAndMerge(vo2);
    }


}