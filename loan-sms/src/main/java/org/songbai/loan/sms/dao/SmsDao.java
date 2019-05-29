package org.songbai.loan.sms.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsSenderTemplate;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.vo.sms.SenderTemplateVO;

import java.util.List;

public interface SmsDao {
//    public void saveSms(Sms sms);
//
//    /**
//     * 获取短信模板
//     *
//     * @param id
//     * @return 短信模板信息
//     */
//
//    public SmsTemplate getTemplate(Integer id, Integer isDelete);

    /**
     * 获取短信发送模板
     *
     * @param templateId
     * @return 短信发送模板信息
     */

    public SmsSenderTemplate getSenderTemplate(@Param("templateId") Integer templateId, @Param("deleted") Integer deleted);


    /**
     * 获取短信发送渠道的 账户 密码等信息
     *
     * @return
     */
    public SmsSender getSenderMessage(@Param("status") Integer status, @Param("agencyId") Integer agencyId);

    SenderTemplateVO getSenderTemplateVO(@Param("agencyId") Integer agencyId, @Param("smsType") Integer smsType, @Param("senderId") Integer senderId);

    SmsTemplate getSmsTemplate(@Param("agencyId") Integer agencyId, @Param("type") Integer type, @Param("vestId") Integer channelId, @Param("senderId") Integer senderId);

    SmsTemplate getDefaultSenderTemplate(@Param("agencyId") Integer agencyId,@Param("type") Integer type, @Param("senderId") Integer senderId);

    SmsSender findSubSenderBySenderId(@Param("senderId") Integer senderId);

    List<SmsSender> findSmsSenderByStart();

}
