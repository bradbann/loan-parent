package org.songbai.loan.user.activity.service.impl;

import org.songbai.loan.model.loan.JoinModel;
import org.songbai.loan.user.activity.dao.JoinDao;
import org.songbai.loan.user.activity.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: qmw
 * Date: 2018/12/25 2:25 PM
 */
@Service
public class JoinServiceImpl implements JoinService {
    @Autowired
    private JoinDao joinDao;

    @Override
    public void addJoin(String phone, String mail) {
        JoinModel joinModel = new JoinModel();
        joinModel.setPhone(phone);
        joinModel.setMail(mail);

        joinDao.insert(joinModel);
    }
}
