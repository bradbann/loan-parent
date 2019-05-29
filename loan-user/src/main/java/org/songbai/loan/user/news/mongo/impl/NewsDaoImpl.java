package org.songbai.loan.user.news.mongo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.model.news.NewsModel;
import org.songbai.loan.user.news.mongo.NewsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class NewsDaoImpl implements NewsDao {
    private Logger logger = LoggerFactory.getLogger(NewsDao.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<NewsModel> findList(NewsModel newsModel, Integer offset, Integer size) {
        Query query = new Query();
        Date date = new Date();
        query.addCriteria(Criteria.where("showStartTime").lt(date).and("showEndTime").gt(date));
        if (newsModel.getType() != null) {
            query.addCriteria(Criteria.where("type").is(newsModel.getType()));
        }
        if (newsModel.getAgencyId() != null) {
            query.addCriteria(Criteria.where("agencyId").is(newsModel.getAgencyId()));
        }
        if (newsModel.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(newsModel.getStatus()));
        }
        if (CollectionUtils.isNotEmpty(newsModel.getVestlist())) {
            query.addCriteria(Criteria.where("vestlist").all(newsModel.getVestlist()));
        }

        if (offset != null && size != null) {
            query.skip(offset);
            query.limit(size);
        }
        query.with(new Sort(Direction.ASC, "index"));
        query.with(new Sort(Direction.DESC, "createTime"));
        logger.info("查询资讯列表{}", query);
        return mongoTemplate.find(query, NewsModel.class);
    }

    @Override
    public NewsModel findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        logger.info("查询资讯详情{}", query);
        return mongoTemplate.findOne(query, NewsModel.class);
    }

}
