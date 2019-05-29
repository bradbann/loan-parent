package org.songbai.loan.service.sms.service;

import com.alibaba.fastjson.JSONObject;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.sms.PushModel;

/**
 * Author: qmw
 * Date: 2018/11/23 2:06 PM
 */
public interface ComPushTemplateService {
    /**
     * 生成借款订单推送模板(不含替换模板)
     */
    PushModel generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN loan);

    /**
     * 生成借款订单推送模板(含替换模板)
     */
    PushModel generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN loan, JSONObject jsonObject);

}
