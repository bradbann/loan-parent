package org.songbai.loan.admin.sms.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.sms.SmsSenderTemplate;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;

import java.util.List;

public interface SmsSenderTemplateDao {

    public void createSenderTemplate(SmsSenderTemplate smsSenderTemplate);

    public void updateSenderTemplate(SmsSenderTemplate smsSenderTemplate);

    public List<SmsSenderTemplate> getAll(
            @Param("agencyId") Integer agencyId, @Param(value = "deleted") Integer deleted,
            @Param(value = "templateId") Integer templateId);

    public SmsSenderTemplate getById(@Param("agencyId") Integer agencyId, @Param(value = "id") Integer id, @Param(value = "deleted") Integer deleted);

    public SmsSenderTemplate getSenderTemplate(@Param(value = "id") Integer id,@Param("agencyId") Integer agencyId);

    public List<SmsSenderTemplateVO> pagingQuery(@Param("agencyId") Integer agencyId,@Param(value = "templateId") Integer templateId,
                                                 @Param(value = "deleted") Integer deleted, @Param(value = "senderId") Integer senderId,
                                                 @Param(value = "limit") Integer limit, @Param(value = "size") Integer size);

    public Integer pagingQuery_count(@Param("agencyId") Integer agencyId,@Param(value = "templateId") Integer templateId,
                                     @Param(value = "deleted") Integer deleted, @Param(value = "senderId") Integer senderId);

    public void remove(@Param(value = "id") Integer id);

    public void removeByTemplateId(@Param(value = "templateId") Integer templateId);

    public SmsSenderTemplate findSenderTemplate(@Param(value = "templateId") Integer templateId,
                                                @Param(value = "senderId") Integer senderId);
}
