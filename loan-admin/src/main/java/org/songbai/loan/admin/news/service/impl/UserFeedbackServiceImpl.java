package org.songbai.loan.admin.news.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.dao.UserFeedbackDao;
import org.songbai.loan.admin.news.model.po.UserFeedPo;
import org.songbai.loan.admin.news.service.UserFeedbackService;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.model.news.UserFeedbackVo;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {

    @Autowired
    UserFeedbackDao userFeedbackDao;
    @Autowired
    ComAgencyService comAgencyService;


    @Override
    public void updateUserFeedback(Integer id) {
        userFeedbackDao.updateUserFeedback(id);
    }

    @Override
    public UserFeedbackModel findUserFeedbackById(Integer id) {
        return userFeedbackDao.queryUserFeedbackById(id);
    }

    @Override
    public Page<UserFeedbackVo> qureyPage(UserFeedPo po) {

        Integer totalCount = userFeedbackDao.qureyCount(po);
        if (totalCount == 0) return new Page<>(po.getPage(), po.getPageSize(), totalCount, new ArrayList<>());

        List<UserFeedbackVo> list = userFeedbackDao.qureyList(po);
        list.forEach(e -> {
            AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
            if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) {
                    e.setVestName(vestModel.getName());
                }
            }
        });

        return new Page<>(po.getPage(), po.getPageSize(), totalCount, list);
    }

}
