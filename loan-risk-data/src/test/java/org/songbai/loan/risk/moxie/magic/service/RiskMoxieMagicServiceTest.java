package org.songbai.loan.risk.moxie.magic.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.risk.LoanRiskDataApplication;
import org.songbai.loan.risk.moxie.magic.model.MagicReportModel;
import org.songbai.loan.risk.moxie.magic.mongo.MagicReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = LoanRiskDataApplication.class)
@RunWith(SpringRunner.class)
public class RiskMoxieMagicServiceTest {


    @Autowired
    RiskMoxieMagicService service;

    @Autowired
    MagicReportRepository reportRepository;



    @Test
    public void getMagicReport() {


        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        MagicReportModel reportModel = reportRepository.getMagicReportModelByIdcard("511321198908168790");

        System.out.println(reportModel);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");



//        service.getMagicReport(
//                "杨海军",
//                "18858279220",
//                "511321198908168790",
//                "1111");
    }
}