package org.songbai.loan.admin.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.sms.SmsTemplate;

import java.util.List;

public interface SmsTemplateDao extends BaseMapper<SmsTemplate> {

    public void createTemplate(SmsTemplate smsTemplate);

    public void updateTemplate(SmsTemplate smsTemplate);

    public List<SmsTemplate> getAll(@Param(value = "deleted") Integer deleted, @Param("agencyId") Integer agencyId);

    public SmsTemplate getById(@Param(value = "id") Integer id, @Param(value = "deleted") Integer deleted, @Param("agencyId") Integer agencyId);

    public SmsTemplate getTemplate(@Param(value = "id") Integer id);

    public List<SmsTemplate> pagingQuery(@Param("agencyId") Integer agencyId, @Param(value = "name") String name, @Param(value = "template") String template,
                                         @Param(value = "deleted") Integer deleted,
                                         @Param("smsType") Integer smsType, @Param("teleCode") String teleCode,
                                         @Param(value = "limit") Integer limit, @Param(value = "size") Integer size);

    public Integer pagingQuery_count(@Param("agencyId") Integer agencyId, @Param(value = "name") String name, @Param(value = "template") String template,
                                     @Param(value = "deleted") Integer deleted,
                                     @Param("smsType") Integer smsType, @Param("teleCode") String teleCode);


    public void remove(@Param("id") Integer id, @Param("agencyId") Integer agencyId);

    public SmsTemplate findTemplate(@Param(value = "name") String name, @Param(value = "template") String template,
                                    @Param("smsType") Integer smsType, @Param("teleCode") String teleCode);

    public List<SmsTemplate> getList(@Param("agencyId") Integer encyId);

    int findStartTemplateByAgencyId(@Param("agencyId") Integer agencyId, @Param("senderId") Integer senderId);

    SmsTemplate findTemplatByAgencyId(SmsTemplate smsTemplate);

    int pagingTemplateQueryCount(@Param("agencyId") Integer agencyId, @Param("page") PageRow page, @Param("vestId") Integer vestId);

    List<SmsSenderTemplateVO> pagingTemplateQueryList(@Param("agencyId") Integer agencyId, @Param("page") PageRow page, @Param("vestId") Integer vestId);

}
