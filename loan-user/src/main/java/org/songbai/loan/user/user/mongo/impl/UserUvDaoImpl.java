package org.songbai.loan.user.user.mongo.impl;

import org.songbai.loan.model.user.UserUvModel;
import org.songbai.loan.user.user.mongo.UserUvDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UserUvDaoImpl implements UserUvDao {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void saveUv(UserUvModel userUvModel) {
        // newsModel.setStatus(0);
        Date date = new Date();
        userUvModel.setCreateTime(date);
        userUvModel.setModifyTime(date);
        mongoTemplate.insert(userUvModel);
    }
}
