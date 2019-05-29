package org.songbai.loan.risk.ctrl;


import lombok.extern.slf4j.Slf4j;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.InnerOnly;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.dao.UserRiskOrderDao;
import org.songbai.loan.risk.mould.variable.ExtractCalcFactory;
import org.songbai.loan.risk.service.UserMouldService;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
@LimitLess
public class TestCtrl {
    @Autowired
    private ExtractCalcFactory extractCalcFactory;

    @Autowired
    UserMouldService userMouldService;

    @Autowired
    private UserRiskOrderDao riskOrderDao;


        /*

    运营商 curl -X POST -d "userId=ef1a870918b945bbbb43d108d897a24b&taskId=498c7870-f386-11e8-976c-00163e10becc&sources=mx_carrier_report" http://localhost:3003/risk/test/extract
    淘宝 curl -X POST -d "userId=ef1a870918b945bbbb43d108d897a24b&taskId=75b15d6a-f388-11e8-9499-00163e0e3263&sources=mx_taobao_report" http://localhost:3003/risk/test/extract

     */


    /**
     * 变量合并
     */
    @PostMapping("extract")
    @InnerOnly
    public Response extract(VariableExtractVO calcVO) {
        try {
            extractCalcFactory.extractAndMerge(calcVO);
        } catch (Exception e) {
            log.error("extract variable error ", e);
            return Response.error(e.getMessage());
        }

        return Response.success();
    }

    /*

     */
    @PostMapping("extract2")
    @InnerOnly
    public Response extract2(VariableExtractVO calcVO) {
        try {

            if (calcVO.getSources().equalsIgnoreCase(VariableConst.VAR_SOURCE_MOXIE_CARRIER)
                    || calcVO.getSources().equalsIgnoreCase(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT)
                    || calcVO.getSources().equalsIgnoreCase(VariableConst.VAR_SOURCE_MOXIE_TAOBAO)
                    || calcVO.getSources().equalsIgnoreCase(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT)) {


                String taskId = riskOrderDao.getTaskIdByUserIdAndSource(calcVO.getUserId(), calcVO.getSources());

                calcVO.setTaskId(taskId);
            }

            extractCalcFactory.extractAndMerge(calcVO);
        } catch (Exception e) {
            log.error("extract variable error ", e);
            return Response.error(e.getMessage());
        }

        return Response.success();
    }


    /*


    curl -X POST -d "thridId=06473fcdb9a04cefa5c2eb146ac01d00&orderNumber=L1811272201B4733SHK" http://localhost:3003/risk/test/overmould

     */
    @PostMapping("overmould")
    @InnerOnly
    public Response overmould(RiskOrderVO orderVO) {


        RiskResultVO resultVO = userMouldService.calc(orderVO);

        return Response.success(resultVO);
    }


}
