package org.songbai.loan.admin.sms.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.model.sms.EmailTemplateModel;

import java.util.List;

public interface EmailTemplateService {
    boolean hasEmailTemplate(Integer type, String teleCode);

    void addTemplate(EmailTemplateModel smsTemplate);

    EmailTemplateModel findTemplate(Integer type, String teleCode);


    void updateTemplate(EmailTemplateModel smsTemplate);


    List<EmailTemplateModel> getTemplate();


    EmailTemplateModel getEmailTemplateById(Integer id);

    Page<EmailTemplateModel> pagingQuery(String name, String template, Integer type, String teleCode, Integer page, Integer pageSize);


    void removeEmailTemplate(Integer id);

}
