package org.songbai.loan.service.sms.service;

import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.model.version.AppVestModel;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: qmw
 * Date: 2018/10/29 下午5:22
 */
public interface ComSmsService {
    void sendMsgCode(String phone, Integer type, String imgCode, HttpServletRequest request, String landCode,Integer sendType);

    /**
     * 校验验证码
     *
     * @param phone
     * @param msgCode
     * @param type
     * @return
     */
    boolean checkMsgCodeForPhone(AppVestModel vestModel,String phone, String msgCode, SmsConst.Type type);

}
