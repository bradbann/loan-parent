package org.songbai.loan.admin.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.sms.SmsSender;

import java.util.List;

public interface SmsSenderDao extends BaseMapper<SmsSender> {

    public void createSenderMessage(SmsSender senderMseeage);

    public void updateSenderMessage(SmsSender senderMseeage);

    public List<SmsSender> pagingQuery(@Param("agencyId") Integer agencyId, @Param("type") Integer type,
                                         @Param("status") Integer status, @Param("isDelete") Integer isDelete, @Param("limit") Integer limit,
                                         @Param("size") Integer size);

    public Integer pagingQuery_count(@Param("agencyId") Integer agencyId, @Param("type") Integer type,
                                     @Param("status") Integer status, @Param("isDelete") Integer isDelete);

    public Integer getSenderByStatus(@Param("agencyId") Integer agencyId, @Param("status") Integer status,
                                     @Param("isDelete") Integer isDelete, @Param("id") Integer id);

    public void remove(@Param("id") Integer id,@Param("agencyId")Integer agencyId);

    public SmsSender getSenderMsg(@Param("agencyId") Integer agencyId, @Param("type") Integer type);

    public SmsSender findById(@Param("agencyId") Integer agencyId, @Param("id") Integer id);

    public void updateStatus(@Param("id") Integer id);

    public List<SmsSender> getList(@Param("agencyId") Integer agencyId);

    SmsSender findSenderByAgencyIdAndType(@Param("agencyId") Integer agencyId, @Param("type") Integer type);

    Integer findStartSenderByAgencyId(@Param("agencyId") Integer agencyId,@Param("senderId") Integer senderId);


    SmsSender getSenderDetail(@Param("id") Integer id, @Param("agencyId") Integer agencyId);


}
