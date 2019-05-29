package org.songbai.loan.risk.moxie.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.InnerOnly;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.risk.moxie.magic.service.RiskMoxieMagicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "data")
@LimitLess
@Slf4j
public class RiskDataCtrl {


    @Autowired
    RiskMoxieMagicService riskMoxieMagicService;

    @RequestMapping("moxieMagic")
    @InnerOnly
    public Response moxieMagic(String userId, String phone, String name, String idcard) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(name) || StringUtils.isEmpty(idcard)) {
            return Response.error("用户数据缺失,不能查询准入报告");
        }

        log.info("获取用户的魔杖报告，userId:{},phone:{},name:{},idcard:{}",userId,phone,name,idcard);

        riskMoxieMagicService.getMagicReport2(name, phone, idcard);

        return Response.success();
    }

}
