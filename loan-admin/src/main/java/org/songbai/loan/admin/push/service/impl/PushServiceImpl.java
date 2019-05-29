package org.songbai.loan.admin.push.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.push.dao.PushSenderDao;
import org.songbai.loan.admin.push.model.vo.PushSenderVO;
import org.songbai.loan.admin.push.service.PushService;
import org.songbai.loan.admin.version.dao.AdminVestDao;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.ActivityRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.sms.PushSenderModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/14 11:26 AM
 */
@Service
public class PushServiceImpl implements PushService {
    @Autowired
    private PushSenderDao pushSenderDao;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    private AdminVestDao adminVestDao;

    @Override
    public void insertPushSender(PushSenderModel model) {
        pushSenderDao.insert(model);
    }

    @Override
    public Page<PushSenderVO> findPushSenderList(PageRow page, Integer agencyId) {
        int count = pushSenderDao.selectSenderCount(agencyId);
        if (count == 0) {
            return new Page<>(page.getPage(), page.getPageSize(), count, new ArrayList<>());
        }

        List<PushSenderVO> list = pushSenderDao.selectSenderList(page, agencyId);
        for (PushSenderVO vo : list) {
            AgencyModel agency = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agency != null) {
                vo.setAgencyName(agency.getAgencyName());
            }

        }
        return new Page<>(page.getPage(), page.getPageSize(), count, list);
    }

    @Override
    public void deletePushSender(Integer id, Integer agencyId) {
        PushSenderModel select = new PushSenderModel();
        select.setId(id);
        select.setAgencyId(agencyId);
        PushSenderModel dbModel = pushSenderDao.selectOne(select);
        if (dbModel == null || dbModel.getDeleted() == CommonConst.DELETED_YES) {
            return;
        }
        if (dbModel.getStatus() == CommonConst.STATUS_VALID) {
            throw new BusinessException(ActivityRespCode.ACTIVITY_CAN_NOT_OPT);
        }

        PushSenderModel update = new PushSenderModel();
        update.setId(id);
        update.setDeleted(CommonConst.DELETED_YES);

        pushSenderDao.updateById(update);

    }

    @Override
    public void updatePushSender(PushSenderModel model) {
        PushSenderModel select = new PushSenderModel();
        select.setId(model.getId());
        select.setAgencyId(model.getAgencyId());
        PushSenderModel dbModel = pushSenderDao.selectOne(select);
        if (dbModel == null || dbModel.getDeleted() == CommonConst.DELETED_YES) {
            return;
        }

        //if (model.getStatus() == CommonConst.STATUS_INVALID) {
        //    int count = adminVestDao.findVestStartPush(model.getId());
        //    if (count > 0) {
        //
        //    }
        //}
        pushSenderDao.updateById(model);
    }

    @Override
    public List<PushSenderModel> findPushSenderSelected(Integer agencyId) {
        return pushSenderDao.findPushSenderSelected(agencyId);
    }

}
