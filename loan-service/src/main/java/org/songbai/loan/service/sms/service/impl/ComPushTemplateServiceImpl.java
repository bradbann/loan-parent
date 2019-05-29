package org.songbai.loan.service.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.sms.PushTemplateModel;
import org.songbai.loan.service.sms.dao.ComPushTemplateDao;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Author: qmw
 * Date: 2018/11/23 2:07 PM
 */
@Service
public class ComPushTemplateServiceImpl implements ComPushTemplateService {
    @Autowired
    private ComPushTemplateDao pushTemplateDao;

    /**
     * 获取推送标题&内容(不含替换模板)
     */
    @Override
    public PushModel generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN loan) {
        PushTemplateModel select = new PushTemplateModel();
        select.setSubType(loan.value);
        PushTemplateModel templateModel = pushTemplateDao.selectOne(select);
        if (templateModel == null) {
            throw new BusinessException(AdminRespCode.PUSH_TEMPLATE_NOT_EXIST);
        }

        PushModel pushModel = new PushModel();
        pushModel.setType(templateModel.getType());
        pushModel.setSubType(templateModel.getSubType());
        pushModel.setTitle(templateModel.getTitle());
        pushModel.setIsJump(templateModel.getIsJump());
        pushModel.setMsg(templateModel.getTemplate());
        return pushModel;
    }

    /**
     * 获取推送标题&内容(含替换模板)
     */
    @Override
    public PushModel generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN loan, JSONObject jsonObject) {
        PushTemplateModel select = new PushTemplateModel();
        select.setSubType(loan.value);
        PushTemplateModel templateModel = pushTemplateDao.selectOne(select);
        if (templateModel == null) {
            throw new BusinessException(AdminRespCode.PUSH_TEMPLATE_NOT_EXIST);
        }

        PushModel pushModel = new PushModel();
        pushModel.setTitle(templateModel.getTitle());
        pushModel.setIsJump(templateModel.getIsJump());
        pushModel.setType(templateModel.getType());
        pushModel.setSubType(templateModel.getSubType());
        if (jsonObject == null) {
            pushModel.setMsg(templateModel.getTemplate());
            return pushModel;
        }

        switch (loan) {
            case AUTH_REJECT:
                pushModel.setMsg(templateModel.getTemplate());
                break;
            case AUTH_PASS:
                pushModel.setMsg(templateModel.getTemplate());
                break;
            case PAY_REJECT:
                pushModel.setMsg(templateModel.getTemplate());
                break;
            case PAY_SUCCESS:
                pushModel.setMsg(pushMsgWrapper(templateModel.getTemplate(), jsonObject));
                break;
            case REPAY_REMIND:
                pushModel.setMsg(pushMsgWrapper(templateModel.getTemplate(), jsonObject));
                break;
            case REPAY_SUCCESS:
                pushModel.setMsg(pushMsgWrapper(templateModel.getTemplate(), jsonObject));
                break;
            case LOAN_OVERDUE:
                pushModel.setMsg(pushMsgWrapper(templateModel.getTemplate(), jsonObject));
                break;
            case AUTH_DEDUCT:
                pushModel.setMsg(pushMsgWrapper(templateModel.getTemplate(), jsonObject));
                break;
        }

        return pushModel;
    }


    /**
     * 替换模板
     * @param template
     * @param jsonObject
     * @return
     */
    private String pushMsgWrapper(String template, JSONObject jsonObject) {

        Set<String> replaceKeys = jsonObject.keySet();
        for (String replaceKey : replaceKeys) {
            template = template.replace("${" + replaceKey + "}", jsonObject.getString(replaceKey));
        }
        return template;
    }
}
