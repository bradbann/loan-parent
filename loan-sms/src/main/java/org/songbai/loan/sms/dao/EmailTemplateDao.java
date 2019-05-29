package org.songbai.loan.sms.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.sms.EmailNotify;
import org.songbai.loan.model.sms.EmailTemplateModel;

public interface EmailTemplateDao {


    public EmailTemplateModel getEmailTemplateModel(@Param("type") Integer type, @Param("teleCode") String teleCode,
                                                    @Param("agencyCode") String agencyCode);
}
