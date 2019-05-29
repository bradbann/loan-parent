package org.songbai.loan.user.news.service.impl;

import org.songbai.loan.model.news.PactModel;
import org.songbai.loan.user.news.mongo.PactDao;
import org.songbai.loan.user.news.service.PactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PactServiceImpl implements PactService {

    @Autowired
    PactDao pactDao;

    @Override
    public PactModel getPactInfoById(String pactId) {
        return pactDao.getPactInfoById(pactId);
    }
}
