//package org.songbai.loan.risk.ctrl;
//
//import org.songbai.loan.risk.service.UserMouldService;
//import org.songbai.loan.risk.vo.RiskResultVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequestMapping("/mould")
//public class UserMouldCtrl {
//
//
//    @Autowired
//    UserMouldService userMouldService;
//
//
//    public RiskResultVO listener(String userId, String orderNumber) {
//        RiskResultVO resultVO = userMouldService.calcAndRecord(userId, orderNumber);
//        resultVO.setOrderNumber(orderNumber);
//
//        return resultVO;
//    }
//
//
//}
