package org.songbai.loan.user.finance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.model.finance.JhPayModel;
import org.songbai.loan.user.finance.service.JhPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jhPayCallBack")
@LimitLess
public class JhPayCallBackCtrl {
    private static final Logger logger = LoggerFactory.getLogger(JhPayCallBackCtrl.class);
    @Autowired
    JhPayService jhPayService;


    @RequestMapping("/jhPayNotify")
    @ResponseBody
    public String jhPayNotify(JhPayModel jhPayModel) { //只有成功才会通知
        String flag = "success";
        logger.info("jhPaySerivce notify receve is succ,info={}", jhPayModel);
        try {
            jhPayService.jhPayNotify(jhPayModel);
        } catch (BusinessException e) {
            flag = "fail";
        }

        return flag;

    }

}
