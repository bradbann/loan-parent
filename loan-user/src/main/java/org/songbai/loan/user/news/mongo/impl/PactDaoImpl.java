package org.songbai.loan.user.news.mongo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.model.news.PactModel;
import org.songbai.loan.user.news.mongo.PactDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PactDaoImpl implements PactDao {
    private Logger logger = LoggerFactory.getLogger(PactDao.class);
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public PactModel getPactInfoById(String pactId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(pactId));
        logger.info("查询用户协议详情{}", query);
        return mongoTemplate.findOne(query, PactModel.class);
    }

}
