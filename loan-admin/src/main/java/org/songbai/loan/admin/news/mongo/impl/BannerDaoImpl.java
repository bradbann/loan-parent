package org.songbai.loan.admin.news.mongo.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.admin.news.mongo.BannerDao;
import org.songbai.loan.model.news.BannerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Repository
public class BannerDaoImpl implements BannerDao {
    private Logger logger = LoggerFactory.getLogger(BannerDaoImpl.class);
    @Autowired
    MongoTemplate mongoTemplate;


    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(BannerModel.class).ensureIndex(new Index("status", Sort.Direction.DESC));
        mongoTemplate.indexOps(BannerModel.class).ensureIndex(new Index("index", Sort.Direction.DESC));
        mongoTemplate.indexOps(BannerModel.class).ensureIndex(new Index("createTime", Sort.Direction.DESC));
        mongoTemplate.indexOps(BannerModel.class).ensureIndex(new Index("showStartTime", Sort.Direction.DESC));
        mongoTemplate.indexOps(BannerModel.class).ensureIndex(new Index("showEndTime", Sort.Direction.DESC));
    }

    @Override
    public void save(BannerModel bannerModel) {
        Date date = new Date();
        bannerModel.setCreateTime(date);
        bannerModel.setUpdateTime(date);
        mongoTemplate.insert(bannerModel);
    }

    @Override
    public BannerModel loadBannerModel(BannerModel bannerModel) {
        Query query = new Query();
        if (bannerModel.getAgencyId() != null) {
            query.addCriteria(Criteria.where("agencyId").is(bannerModel.getAgencyId()));
        }

        if (bannerModel.getId() != null) {
            query.addCriteria(Criteria.where("id").is(bannerModel.getId()));
        }
        return mongoTemplate.findOne(query, BannerModel.class);
    }

    @Override
    public List<BannerModel> findByPage(BannerModel bannerModel, Integer offset, Integer pageSize) {
        Query query = queryWrapper(bannerModel);
        query.with(new Sort(Sort.Direction.DESC, "index")).with(new Sort(Sort.Direction.DESC, "createTime"));
        query.skip(offset);
        query.limit(pageSize);
        return mongoTemplate.find(query, BannerModel.class);
    }

    @Override
    public Integer findRows(BannerModel bannerModel) {
        Query query = queryWrapper(bannerModel);
        return (int) mongoTemplate.count(query, BannerModel.class);

    }

    @Override
    public void delete(String ids) {
        Query query = Query.query(Criteria.where("id").in(Arrays.asList(ids.split(","))));

//        logger.info("delete news for :{}", query);

        mongoTemplate.remove(query, BannerModel.class);
    }


    @Override
    public List<BannerModel> findListByIds(String ids) {
        Query query = Query.query(Criteria.where("id").in(Arrays.asList(ids.split(","))));
        return mongoTemplate.find(query, BannerModel.class);
    }

    @Override
    public void updateBannerById(BannerModel bannerModel) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(bannerModel.getId()));

        Update update = wrapperUpdate(bannerModel);

        mongoTemplate.updateFirst(query, update, BannerModel.class);
    }

    private Update wrapperUpdate(BannerModel bannerModel) {
        Update update = new Update();
        if (StringUtils.isNotEmpty(bannerModel.getTitle())) {
            update.set("title", bannerModel.getTitle());
        }
        if (StringUtils.isNotEmpty(bannerModel.getSubTitle())) {
            update.set("subTitle", bannerModel.getSubTitle());
        }
        if (StringUtils.isNotEmpty(bannerModel.getContent())) {
            update.set("content", bannerModel.getContent());
        }
        if (bannerModel.getJumpType() != null) {
            update.set("jumpType", bannerModel.getJumpType());
        }
        if (bannerModel.getStatus() != null) {
            update.set("status", bannerModel.getStatus());
        }
        if (bannerModel.getIndex() != null) {
            update.set("index", bannerModel.getIndex());
        }
        if (bannerModel.getShowcase() != null) {
            update.set("showcase", bannerModel.getShowcase());
        }
        if (StringUtils.isNotEmpty(bannerModel.getExcludeVersion())) {
            update.set("excludeVersion", bannerModel.getExcludeVersion());
        }
        if (StringUtils.isNotEmpty(bannerModel.getIncludeVersion())) {
            update.set("includeVersion", bannerModel.getIncludeVersion());
        }
        if (bannerModel.getScopes() != null) {
            update.set("scopes", bannerModel.getScopes());
        }
        if (bannerModel.getVestlist() != null) {
            update.set("vestlist", bannerModel.getVestlist());
        }
        if (bannerModel.getShowStartTime() != null) {
            update.set("showStartTime", bannerModel.getShowStartTime());
        }
        if (null != bannerModel.getShowEndTime()) {
            update.set("showEndTime", bannerModel.getShowEndTime());
        }
        if (StringUtils.isNotEmpty(bannerModel.getJumpContent())) {
            update.set("jumpContent", bannerModel.getJumpContent());
        }

        update.set("operator", bannerModel.getOperator());
        update.set("updateTime", new Date());

        return update;
    }

    private Query queryWrapper(BannerModel bannerModel) {
        QueryBuilder queryBuilder = new QueryBuilder();
        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("id", 1);
        fieldsObject.put("agencyId", 1);
        fieldsObject.put("title", 1);
        fieldsObject.put("subTitle", 1);
        fieldsObject.put("content", 1);
        fieldsObject.put("jumpType", 1);
        fieldsObject.put("status", 1);
        fieldsObject.put("index", 1);
        fieldsObject.put("includeVersion", 1);
        fieldsObject.put("includeAgency", 1);
        fieldsObject.put("scopes", 1);
        fieldsObject.put("showStartTime", 1);
        fieldsObject.put("operator", 1);
        fieldsObject.put("jumpContent", 1);

        BasicQuery query = new BasicQuery(queryBuilder.get(), fieldsObject);
        if (bannerModel.getTitle() != null) {
            query.addCriteria(Criteria.where("title").regex(".*?" + bannerModel.getTitle() + ".*"));
        }
        if (bannerModel.getAgencyId() != null) {
            query.addCriteria(Criteria.where("agencyId").is(bannerModel.getAgencyId()));
        }

        if (bannerModel.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(bannerModel.getStatus()));
        }

        if (bannerModel.getShowcase() != null) {
            query.addCriteria(Criteria.where("showcase").is(bannerModel.getShowcase()));
        }

        if (bannerModel.getScopes() != null) {
            query.addCriteria(Criteria.where("scopes").all(bannerModel.getScopes()));
        }
        return query;
    }

}
