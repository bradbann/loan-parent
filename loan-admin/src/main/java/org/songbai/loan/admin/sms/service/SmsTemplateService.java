package org.songbai.loan.admin.sms.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.sms.SmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SmsTemplateService {

    public void addTemplate(SmsTemplate smsTemplate);

    public void updateTemplate(SmsTemplate smsTemplate);

    public List<SmsTemplate> getTemplate(Integer agencyId);

    public SmsTemplate getById(Integer id,Integer agencyId);

    Page<SmsTemplate> pagingQuery(Integer agencyId, String name, String template,
                                  Integer smsType, String teleCode,
                                  Integer pageIndex, Integer pageSize);

    public void remove(Integer id, Integer agencyId);

    boolean hasSmsTemplate(Integer agencyId,String name, String teleCode);

    SmsTemplate findTemplate(String name, String template, Integer smsType, String teleCode);

    public List<SmsTemplate> getList(Integer agencyId);

    SmsTemplate findTemplatByAgencyId(SmsTemplate smsTemplate);

    Page<SmsSenderTemplateVO> pagingTemplateQuery(Integer agencyId, PageRow page,Integer vestId);

}
