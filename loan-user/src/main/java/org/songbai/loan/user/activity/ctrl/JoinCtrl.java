package org.songbai.loan.user.activity.ctrl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.user.activity.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: qmw
 * Date: 2018/12/25 2:24 PM
 */
@RestController
@RequestMapping("/join")
public class JoinCtrl {

    @Autowired
    private JoinService joinService;

    @LimitLess
    @PostMapping("/service")
    public Response joinuUs(String phone,String mail) {
        Assert.hasLength(phone, LocaleKit.get("common.param.notnull", "phone"));
        Assert.hasLength(mail, LocaleKit.get("common.param.notnull", "mail"));
        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }
        if (!Regular.checkEmail(mail)) {
            throw new BusinessException(UserRespCode.EMAIL_FORMAT_ERROR);
        }
        joinService.addJoin(phone, mail);
        return Response.success();
    }
}

