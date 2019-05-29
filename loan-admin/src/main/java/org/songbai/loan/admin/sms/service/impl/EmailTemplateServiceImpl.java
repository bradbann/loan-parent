package org.songbai.loan.admin.sms.service.impl;

import java.util.List;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.sms.dao.EmailTemplateDao;
import org.songbai.loan.admin.sms.helper.TemplateNotifyHelper;
import org.songbai.loan.admin.sms.service.EmailTemplateService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.sms.EmailTemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Autowired
    private EmailTemplateDao emailTemplateDao;

    @Autowired
    TemplateNotifyHelper notifyHelper;

    @Override
    public boolean hasEmailTemplate(Integer type, String teleCode) {


        return findTemplate(type, teleCode) != null;
    }

    @Override
    public void addTemplate(EmailTemplateModel smsTemplate) {

        emailTemplateDao.insert(smsTemplate);


        notifyHelper.notifyEmailTemplate(smsTemplate.getId());
    }

    @Override
    public EmailTemplateModel findTemplate(Integer type, String teleCode) {


        EmailTemplateModel query = new EmailTemplateModel();

        query.setType(type);
        query.setTeleCode(teleCode);
        query.setDeleted(CommonConst.DELETED_NO);


        return emailTemplateDao.selectOne(query);
    }

    @Override
    public void updateTemplate(EmailTemplateModel smsTemplate) {

        emailTemplateDao.updateById(smsTemplate);

        notifyHelper.notifyEmailTemplate(smsTemplate.getId());
    }

    @Override
    public List<EmailTemplateModel> getTemplate() {
        EmailTemplateModel query = new EmailTemplateModel();
        query.setDeleted(CommonConst.DELETED_NO);
        return emailTemplateDao.selectList(new EntityWrapper<>(query));
    }

    @Override
    public EmailTemplateModel getEmailTemplateById(Integer id) {
        return emailTemplateDao.selectById(id);
    }

    @Override
    public Page<EmailTemplateModel> pagingQuery(String name, String template,
                                                Integer type, String teleCode,
                                                Integer page, Integer pageSize) {
        EntityWrapper wrapper = new EntityWrapper();

        wrapper.eq("deleted", CommonConst.DELETED_NO);
        if (StringUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        if (StringUtil.isNotEmpty(template)) {
            wrapper.like("template", template);
        }
        if (type != null) {
            wrapper.eq("type", type);
        }
        if (StringUtil.isNotEmpty(teleCode)) {
            wrapper.eq("tele_code", teleCode);
        }

        wrapper.orderBy("create_time", false);


        Pagination pagination = new Pagination(page + 1, pageSize);

        List<EmailTemplateModel> list = emailTemplateDao.selectPage(pagination, wrapper);


        Page<EmailTemplateModel> result = new Page<>(pagination.getOffset(), pageSize, pagination.getTotal());

        result.setData(list);
        return result;
    }

    @Override
    public void removeEmailTemplate(Integer id) {
        EmailTemplateModel query = new EmailTemplateModel();

        query.setId(id);
        query.setDeleted(CommonConst.DELETED_YES);

        emailTemplateDao.updateById(query);

        notifyHelper.notifyEmailTemplate(id);
    }
}
