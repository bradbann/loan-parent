package org.songbai.loan.admin.sms.service;


import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.sms.model.SmsSenderVO;
import org.songbai.loan.model.sms.SmsSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SmsSenderService {

    public void createSenderMessage(SmsSender senderMseeage);

    public void updateSenderMessage(SmsSender senderMseeage);

    public Page<SmsSender> pagingQuery(Integer agencyId, Integer type, Integer pageIndex,
                                       Integer pageSize);

    public Integer getSenderByStatus(Integer agencyId, Integer id);

    public boolean hasSender(Integer agencyId, Integer type);

    public Integer hasSenderIsActive(Integer agencyId, Integer type);

    public void remove(Integer agencyId, Integer id);

    public SmsSender getSenderMsg(Integer agencyId, Integer type);

    public void updateStatus(Integer id);

    public SmsSender findById(Integer agencyId, Integer id);

    List<SmsSenderVO> getSenders(Integer agencyId);

    public List<SmsSender> getList(Integer agencyId);


    boolean validateSenderExist(Integer agencyId, Integer type);

    int findStartTemplateByAgencyId(Integer agencyId,Integer senderId);

    Integer findStartSenderByAgencyId(Integer agencyId,Integer senderId);

    SmsSender getSenderDetail(Integer id, Integer agencyId);


}
