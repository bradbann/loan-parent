package org.songbai.loan.admin.sms.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.sms.dao.SmsSenderTemplateDao;
import org.songbai.loan.admin.sms.helper.TemplateNotifyHelper;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.admin.sms.service.SmsSenderTemplateService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.sms.SmsSenderTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsSenderTemplateServiceImpl implements SmsSenderTemplateService {

    @Autowired
    SmsSenderTemplateDao smsSenderTemplateDao;

    @Autowired
    TemplateNotifyHelper notifyHelper;

    @Override
    public void addSenderTemplate(SmsSenderTemplate smsSenderTemplate) {
        smsSenderTemplateDao.createSenderTemplate(smsSenderTemplate);
        sendMsg(smsSenderTemplate);
    }

    @Override
    public void updateSenderTemplate(SmsSenderTemplate smsSenderTemplate) {
        smsSenderTemplateDao.updateSenderTemplate(smsSenderTemplate);
        SmsSenderTemplate senderTemplate = smsSenderTemplateDao.getSenderTemplate(smsSenderTemplate.getId(),smsSenderTemplate.getAgencyId());
        sendMsg(senderTemplate);

    }

    @Override
    public List<SmsSenderTemplate> getSenderTemplate(Integer agencyId) {
        List<SmsSenderTemplate> smsSenderTemplates = smsSenderTemplateDao.getAll(agencyId, CommonConst.DELETED_NO,
                null);
        return smsSenderTemplates;
    }

    @Override
    public SmsSenderTemplate getById(Integer agencyId, Integer id) {
        SmsSenderTemplate smsSenderTemplate = smsSenderTemplateDao.getById(agencyId, id, CommonConst.DELETED_NO);
        return smsSenderTemplate;
    }

    @Override
    public Page<SmsSenderTemplateVO> pagingQuery(Integer agencyId, Integer templateId, Integer type, Integer pageIndex,
                                                 Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        List<SmsSenderTemplateVO> smsSenderTemplates = smsSenderTemplateDao.pagingQuery(agencyId, templateId,
                CommonConst.DELETED_NO, type, limit, pageSize);
        int totalCOunt = smsSenderTemplateDao.pagingQuery_count(agencyId, templateId, CommonConst.DELETED_NO,
                type);
        Page<SmsSenderTemplateVO> resultPage = new Page<>(pageIndex, pageSize, totalCOunt);
        resultPage.setData(smsSenderTemplates);
        return resultPage;
    }

    @Override
    public void remove(Integer id, Integer agencyId) {
        smsSenderTemplateDao.remove(id);
        SmsSenderTemplate smsSenderTemplate = smsSenderTemplateDao.getSenderTemplate(id, agencyId);
        sendMsg(smsSenderTemplate);
    }

    /**
     * 发送消息
     *
     * @param senderTemplate
     */
    private void sendMsg(SmsSenderTemplate senderTemplate) {

        notifyHelper.notifySmsTemplateSender(senderTemplate.getId());
    }

    @Override
    public boolean hasSenderTemplate(Integer agencyId, Integer templateId, Integer senderId) {
        return smsSenderTemplateDao.pagingQuery_count(agencyId, templateId, CommonConst.DELETED_NO, senderId) > 0;

    }

    @Override
    public SmsSenderTemplate findSenderTemplate(Integer templateId, Integer type) {
        return smsSenderTemplateDao.findSenderTemplate(templateId, type);
    }
}
