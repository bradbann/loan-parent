package org.songbai.loan.admin.sms.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.admin.sms.dao.SmsSenderTemplateDao;
import org.songbai.loan.admin.sms.dao.SmsTemplateDao;
import org.songbai.loan.admin.sms.helper.TemplateNotifyHelper;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.admin.sms.model.SmsSenderVO;
import org.songbai.loan.admin.sms.service.SmsTemplateService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {
    @Autowired
    SmsTemplateDao smsTemplateDao;

    @Autowired
    SmsSenderTemplateDao smsSenderTemplateDao;
    @Autowired
    private ComAgencyService comAgencyService;

    @Autowired
    TemplateNotifyHelper notifyHelper;

    @Override
    public void addTemplate(SmsTemplate smsTemplate) {
        smsTemplateDao.insert(smsTemplate);

        notifyHelper.notifySmsTemplate(smsTemplate.getId());
    }

    @Override
    public void updateTemplate(SmsTemplate smsTemplate) {
        smsTemplateDao.updateById(smsTemplate);

        notifyHelper.notifySmsTemplate(smsTemplate.getId());
    }

    @Override
    public List<SmsTemplate> getTemplate(Integer agencyId) {
        List<SmsTemplate> smsTemplates = smsTemplateDao.getAll(CommonConst.DELETED_NO, agencyId);
        return smsTemplates;
    }

    @Override
    public SmsTemplate getById(Integer id, Integer agencyId) {
        SmsTemplate smsTemplate = smsTemplateDao.getById(id, CommonConst.DELETED_NO, agencyId);
        return smsTemplate;
    }

    @Override
    public Page<SmsTemplate> pagingQuery(Integer agencyId, String name, String template,
                                         Integer smsType, String teleCode,
                                         Integer pageIndex, Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0 * pageSize;
        List<SmsTemplate> smsTemplates = smsTemplateDao.pagingQuery(agencyId, name, template, CommonConst.DELETED_NO,
                smsType, teleCode,
                limit, pageSize);
        int totalCOunt = smsTemplateDao.pagingQuery_count(agencyId, name, template, CommonConst.DELETED_NO, smsType, teleCode);
        Page<SmsTemplate> resultPage = new Page<SmsTemplate>(pageIndex, pageSize, totalCOunt);
        resultPage.setData(smsTemplates);
        return resultPage;
    }

    /**
     * 进行软删除
     */
    @Override
    public void remove(Integer id, Integer agencyId) {
        smsTemplateDao.remove(id, agencyId);

        //smsSenderTemplateDao.removeByTemplateId(id);

        notifyHelper.notifySmsTemplate(id);
    }


    @Override
    public boolean hasSmsTemplate(Integer agencyId, String name, String teleCode) {
        return smsTemplateDao.pagingQuery_count(agencyId, name, null, CommonConst.DELETED_NO, null, teleCode) > 0;
    }

    @Override
    public SmsTemplate findTemplate(String name, String template, Integer smsType, String teleCode) {
        return smsTemplateDao.findTemplate(name, template, smsType, teleCode);
    }

    @Override
    public List<SmsTemplate> getList(Integer agencyId) {
        return smsTemplateDao.getList(agencyId);
    }

    @Override
    public SmsTemplate findTemplatByAgencyId(SmsTemplate smsTemplate) {

        return smsTemplateDao.findTemplatByAgencyId(smsTemplate);
    }

    @Override
    public Page<SmsSenderTemplateVO> pagingTemplateQuery(Integer agencyId, PageRow page,Integer vestId ) {
        int count = smsTemplateDao.pagingTemplateQueryCount(agencyId, page,vestId);
        if (count <= 0) {
            return new Page<>(page.getPage(), page.getPageSize(), 0, new ArrayList<>());
        }
        List<SmsSenderTemplateVO> list = smsTemplateDao.pagingTemplateQueryList(agencyId, page,vestId);
        for (SmsSenderTemplateVO s : list) {
            if (s.getAgencyId() != null) {
                AgencyModel agencyById = comAgencyService.findAgencyById(s.getAgencyId());
                s.setAgencyName(agencyById.getAgencyName());
            }
        }

        return new Page<>(page.getPage(), page.getPageSize(), count, list);
    }

}
