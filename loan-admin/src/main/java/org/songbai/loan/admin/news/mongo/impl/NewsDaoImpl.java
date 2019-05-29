package org.songbai.loan.admin.news.mongo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.admin.news.model.po.NewsVo;
import org.songbai.loan.admin.news.mongo.NewsDao;
import org.songbai.loan.model.news.NewsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class NewsDaoImpl implements NewsDao {
    private Logger logger = LoggerFactory.getLogger(NewsDao.class);
    @Autowired
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("createTime", Sort.Direction.DESC));
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("status", Sort.Direction.DESC));
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("type", Sort.Direction.DESC));
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("index", Sort.Direction.DESC));
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("showStartTime", Sort.Direction.DESC));
        mongoTemplate.indexOps(NewsModel.class).ensureIndex(new Index("showEndTime", Sort.Direction.DESC));
    }

    @Override
    public List<NewsVo> findByPage(NewsModel newsModel, Integer offset, Integer pageSize) {
        Criteria criteria = new Criteria();
        Query query = new Query(criteria);
        if (newsModel.getTitle() != null) {
            criteria = Criteria.where("title").regex(".*?" + newsModel.getTitle() + ".*");
            query.addCriteria(criteria);
        }
        if (newsModel.getAgencyId() != null) {
            query.addCriteria(Criteria.where("agencyId").is(newsModel.getAgencyId()));
        }
        if (newsModel.getSummary() != null) {
            criteria = Criteria.where("summary").regex(".*?" + newsModel.getSummary() + ".*");
            query.addCriteria(criteria);
        }

        if (newsModel.getStyle() != null) {
            criteria = Criteria.where("style").is(newsModel.getStyle());
            query.addCriteria(criteria);
        }
        if (newsModel.getStatus() != null) {
            criteria = Criteria.where("status").is(newsModel.getStatus());
            query.addCriteria(criteria);
        }

        if (newsModel.getType() != null) {
            query.addCriteria(Criteria.where("type").is(newsModel.getType()));
        }

        query.with(new Sort(Sort.Direction.DESC, "index")).with(new Sort(Sort.Direction.DESC, "createTime"));
        query.skip(offset);
        query.limit(pageSize);
//        logger.info("{}", query);
        return mongoTemplate.find(query, NewsVo.class);
    }

    @Override
    public Integer findRows(NewsModel newsModel) {
        Criteria criteria = new Criteria();
        Query query = new Query(criteria);
        if (newsModel.getTitle() != null) {
            criteria = Criteria.where("title").regex(".*?" + newsModel.getTitle() + ".*");
            query.addCriteria(criteria);
        }
        if (newsModel.getAgencyId() != null) {
            query.addCriteria(Criteria.where("agencyId").is(newsModel.getAgencyId()));
        }

        if (newsModel.getSummary() != null) {
            criteria = Criteria.where("summary").regex(".*?" + newsModel.getSummary() + ".*");
            query.addCriteria(criteria);
        }
        if (newsModel.getStyle() != null) {
            criteria = Criteria.where("style").is(newsModel.getStyle());
            query.addCriteria(criteria);
        }
        if (newsModel.getStatus() != null) {
            criteria = Criteria.where("status").is(newsModel.getStatus());
            query.addCriteria(criteria);
        }
        return (int) mongoTemplate.count(query, NewsModel.class);
    }

    @Override
    public NewsModel find(NewsModel newsModel) {
        Query query = new Query();
        if (null != newsModel.getId()) {
            query.addCriteria(Criteria.where("id").is(newsModel.getId()));
        }

        if (null != newsModel.getAgencyId()) {
            query.addCriteria(Criteria.where("agencyId").is(newsModel.getAgencyId()));
        }
        return mongoTemplate.findOne(query, NewsModel.class);
    }

    @Override
    public void save(NewsModel newsModel) {
        // newsModel.setStatus(0);
        Date date = new Date();
        newsModel.setCreateTime(date);
        newsModel.setUpdateTime(date);
        mongoTemplate.insert(newsModel);
    }

    @Override
    public void update(NewsModel newsModel) {
        Update update = new Update();
        if (newsModel.getTitle() != null) {
            update.set("title", newsModel.getTitle());
        }
        if (newsModel.getSummary() != null) {
            update.set("summary", newsModel.getSummary());
        }
        if (newsModel.getType() != null) {
            update.set("type", newsModel.getType());
        }
        if (newsModel.getStyle() != null) {
            update.set("style", newsModel.getStyle());
        }
        if (newsModel.getContent() != null) {
            update.set("content", newsModel.getContent());
        }
        if (newsModel.getCover() != null) {
            update.set("cover", newsModel.getCover());
        }
        if (newsModel.getStatus() != null) {
            update.set("status", newsModel.getStatus());
        }
        if (newsModel.getOperator() != null) {
            update.set("operator", newsModel.getOperator());
        }
        if (newsModel.getIndex() != null) {
            update.set("index", newsModel.getIndex());
        }
        if (newsModel.getSource() != null) {
            update.set("source", newsModel.getSource());
        }

        if (newsModel.getFormat() != null) {
            update.set("format", newsModel.getFormat());
        }
        if (newsModel.getShowStartTime() != null) {
            update.set("showStartTime", newsModel.getShowStartTime());
        }
        if (newsModel.getScopes() != null) {
            update.set("scopes", newsModel.getScopes());
        }
        if (newsModel.getVestlist() != null) {
            update.set("vestlist", newsModel.getVestlist());
        }
        if (newsModel.getShowEndTime() != null) {
            update.set("showEndTime", newsModel.getShowEndTime());
        }
        update.set("operator", newsModel.getOperator());
        update.set("updateTime", new Date());


        Query query = new Query(Criteria.where("id").is(newsModel.getId()));
        mongoTemplate.updateFirst(query, update, NewsModel.class);
    }

    @Override
    public void delete(String ids) {
        Query query = Query.query(Criteria.where("id").in(Arrays.asList(ids.split(","))));
        mongoTemplate.remove(query, NewsModel.class);
    }

    @Override
    public List<NewsModel> findListByIds(String ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(Arrays.asList(ids.split(","))));
        return mongoTemplate.find(query, NewsModel.class);
    }

}
