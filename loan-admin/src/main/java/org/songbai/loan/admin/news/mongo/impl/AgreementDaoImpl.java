package org.songbai.loan.admin.news.mongo.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.mongo.AgreementDao;
import org.songbai.loan.model.news.AgreementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class AgreementDaoImpl implements AgreementDao {

    @Autowired
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(AgreementModel.class).ensureIndex(new Index("code", Sort.Direction.DESC));
    }


    @Override
    public Page<AgreementModel> findAgreementByPage(AgreementModel agreementModel, Integer index, Integer size) {
        List<AgreementModel> list = new ArrayList<>();
        Query dbQuery = new Query();

        if (StringUtils.isNoneBlank(agreementModel.getTitle())) {
            dbQuery.addCriteria(Criteria.where("title").regex(".*?" + agreementModel.getTitle() + ".*"));
        }
        if (agreementModel.getAgencyId() != null) {
            dbQuery.addCriteria(Criteria.where("agencyId").is(agreementModel.getAgencyId()));
        }

        if (StringUtils.isNoneBlank(agreementModel.getCode())) {
            dbQuery.addCriteria(Criteria.where("code").is(agreementModel.getCode()));
        }

        long count = mongoTemplate.count(dbQuery, AgreementModel.class);
        if (count > 0) {
            dbQuery.skip(index);// skip相当于从那条记录开始
            dbQuery.limit(size);// 从skip开始,取多少条记录
            dbQuery.with(new Sort(Sort.Direction.DESC, "createTime"));
            list = mongoTemplate.find(dbQuery, AgreementModel.class);
        }
        Page<AgreementModel> page = new Page<>(index, size, (int) count, list);
        return page;
    }

    @Override
    public AgreementModel findAgreementById(String id) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(id)) {
            query.addCriteria(Criteria.where("id").is(id));
        }
        return mongoTemplate.findOne(query, AgreementModel.class);
    }

    @Override
    public AgreementModel findAgreementByCode(String code, Integer agencyId) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(code)) {
            query.addCriteria(Criteria.where("code").is(code));
        }
        query.addCriteria(Criteria.where("agencyId").is(agencyId));

        return mongoTemplate.findOne(query, AgreementModel.class);
    }

    @Override
    public void updateAgreement(AgreementModel agreementModel) {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(agreementModel.getId()));

        Update update = new Update();

        update.set("modifyTime", new Date());
        update.set("code", agreementModel.getCode());
        update.set("title", agreementModel.getTitle());
        update.set("format", agreementModel.getFormat());
        update.set("content", agreementModel.getContent());
        update.set("operator", agreementModel.getOperator());
        update.set("author", agreementModel.getAuthor());
        update.set("summary", agreementModel.getSummary());
        mongoTemplate.updateFirst(query, update, AgreementModel.class);
    }

    @Override
    public void deleteAgreementById(Integer agencyId, String... ids) {

        if (ids != null) {
            for (String id : ids) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(id));
                query.addCriteria(Criteria.where("agencyId").is(agencyId));
                mongoTemplate.remove(query, AgreementModel.class);
            }
        }

    }

    @Override
    public void insertAgreement(AgreementModel agreementModel) {
        Date date = new Date();
        agreementModel.setCreateTime(date);
        agreementModel.setModifyTime(date);
        mongoTemplate.save(agreementModel);
    }


}
