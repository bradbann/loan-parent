package org.songbai.loan.admin.sms.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.model.sms.SmsSenderTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SmsSenderTemplateService {
    public void addSenderTemplate(SmsSenderTemplate smsSenderTemplate);

    public void updateSenderTemplate(SmsSenderTemplate smsSenderTemplate);

    public List<SmsSenderTemplate> getSenderTemplate(Integer agencyId);

    public SmsSenderTemplate getById(Integer agencyId, Integer id);

    public Page<SmsSenderTemplateVO> pagingQuery(Integer agencyId, Integer templateId, Integer type, Integer pageIndex, Integer pageSize);

    public void remove(Integer id,Integer agencyId);

    public boolean hasSenderTemplate(Integer agencyId,Integer templateId, Integer senderId);

    public SmsSenderTemplate findSenderTemplate(Integer templateId, Integer type);
}
