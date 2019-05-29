package org.songbai.loan.admin.sms.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.admin.sms.dao.SmsSenderDao;
import org.songbai.loan.admin.sms.dao.SmsTemplateDao;
import org.songbai.loan.admin.sms.helper.TemplateNotifyHelper;
import org.songbai.loan.admin.sms.model.SmsSenderVO;
import org.songbai.loan.admin.sms.service.SmsSenderService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmsSenderServiceImpl implements SmsSenderService {

    @Autowired
    SmsSenderDao smsSenderDao;
    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @Autowired
    TemplateNotifyHelper notifyHelper;
    @Autowired
    private ComAgencyService comAgencyService;

    @Override
    public void createSenderMessage(SmsSender senderMseeage) {
        smsSenderDao.insert(senderMseeage);
        sendMsg(senderMseeage);
    }

    @Override
    public void updateSenderMessage(SmsSender senderMseeage) {
        smsSenderDao.updateById(senderMseeage);
        SmsSender message = smsSenderDao.findById(senderMseeage.getAgencyId(), senderMseeage.getId());
        sendMsg(message);
    }

    @Override
    public Page<SmsSender> pagingQuery(Integer agencyId, Integer type, Integer pageIndex,
                                       Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0 * pageSize;
        List<SmsSender> page = smsSenderDao.pagingQuery(agencyId, type, null,
                CommonConst.DELETED_NO, limit, pageSize);
        int totalCount = smsSenderDao.pagingQuery_count(agencyId, type, null, CommonConst.DELETED_NO);
        Page<SmsSender> resultPage = new Page<SmsSender>(pageIndex, pageSize, totalCount);
        resultPage.setData(page);
        return resultPage;
    }

    /**
     * 添加时验证 是否已存在该短信渠道
     */
    @Override
    public boolean hasSender(Integer agencyId, Integer type) {
        return smsSenderDao.pagingQuery_count(agencyId, type, null, CommonConst.DELETED_NO) > 0;
    }

    /**
     * 验证是否存在已激活的渠道
     */
    @Override
    public Integer hasSenderIsActive(Integer agencyId, Integer type) {
        return smsSenderDao.pagingQuery_count(agencyId, type, CommonConst.STATUS_VALID, CommonConst.DELETED_NO);
    }

    /**
     * 验证除他本身 是否还存在激活渠道
     */
    @Override
    public Integer getSenderByStatus(Integer agencyId, Integer id) {
        return smsSenderDao.getSenderByStatus(agencyId, CommonConst.STATUS_VALID, CommonConst.DELETED_NO, id);
    }

    /**
     * 发送信息
     *
     * @param smsSender
     */
    private void sendMsg(SmsSender smsSender) {
        notifyHelper.notifySmsSender(smsSender.getId());
    }

    @Override
    public void remove(Integer agencyId, Integer id) {
        smsSenderDao.remove(id, agencyId);
        SmsSender message = smsSenderDao.findById(agencyId, id);
        sendMsg(message);
    }

    /**
     * 验证该渠道下是否已存在该渠道
     */
    @Override
    public SmsSender getSenderMsg(Integer agencyId, Integer type) {
        return smsSenderDao.getSenderMsg(agencyId, type);
    }

    @Override
    public void updateStatus(Integer id) {
        smsSenderDao.updateStatus(id);

    }

    @Override
    public SmsSender findById(Integer agencyId, Integer id) {
        return smsSenderDao.findById(agencyId, id);
    }


    @Override
    public List<SmsSenderVO> getSenders(Integer agencyId) {
        List<SmsSender> page = smsSenderDao.pagingQuery(agencyId, null, null,
                CommonConst.DELETED_NO, 0, 1000);

        return page.stream().map(s -> {
            SmsSenderVO vo = new SmsSenderVO();
            BeanUtil.copyNotNullProperties(s, vo);
            if (s.getAgencyId() != null) {
                AgencyModel agencyById = comAgencyService.findAgencyById(s.getAgencyId());
                vo.setAgencyName(agencyById.getAgencyName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SmsSender> getList(Integer agencyId) {
        return smsSenderDao.getList(agencyId);
    }

    @Override
    public boolean validateSenderExist(Integer agencyId, Integer type) {
        SmsSender sender = smsSenderDao.findSenderByAgencyIdAndType(agencyId, type);
        return sender != null;
    }

    @Override
    public int findStartTemplateByAgencyId(Integer agencyId, Integer senderId) {

        return smsTemplateDao.findStartTemplateByAgencyId(agencyId, senderId);
    }

    @Override
    public Integer findStartSenderByAgencyId(Integer agencyId, Integer senderId) {
        return smsSenderDao.findStartSenderByAgencyId(agencyId, senderId);
    }

    @Override
    public SmsSender getSenderDetail(Integer id, Integer agencyId) {

        return smsSenderDao.getSenderDetail(id, agencyId);
    }


}
