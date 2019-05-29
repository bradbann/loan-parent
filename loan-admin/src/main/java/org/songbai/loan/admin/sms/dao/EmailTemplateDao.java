package org.songbai.loan.admin.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.sms.EmailTemplateModel;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateDao extends BaseMapper<EmailTemplateModel> {
}
