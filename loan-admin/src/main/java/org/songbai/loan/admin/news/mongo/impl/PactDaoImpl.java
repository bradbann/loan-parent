package org.songbai.loan.admin.news.mongo.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.PactVo;
import org.songbai.loan.admin.news.mongo.PactDao;
import org.songbai.loan.model.news.PactModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
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
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class PactDaoImpl implements PactDao {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private ComAgencyService comAgencyService;

    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(PactModel.class).ensureIndex(new Index("createTime", Sort.Direction.DESC));
    }

    @Override
    public PactModel findPactByCode(String code, Integer agencyId) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(code)) {
            query.addCriteria(Criteria.where("code").is(code));
        }
        query.addCriteria(Criteria.where("agencyId").is(agencyId));

        return mongoTemplate.findOne(query, PactModel.class);
    }

    @Override
    public void addPact(PactModel pactModel) {
        Date date = new Date();
        pactModel.setCreateTime(date);
        pactModel.setModifyTime(date);
        mongoTemplate.save(pactModel);
    }

    @Override
    public PactModel findPactById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, PactModel.class);
    }

    @Override
    public void updatePact(PactModel model) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(model.getId()));

        Update update = new Update();

        update.set("modifyTime", new Date());
        update.set("code", model.getCode());
        update.set("title", model.getTitle());
        update.set("content", model.getContent());
        update.set("operator", model.getOperator());
        update.set("type", model.getType());
        mongoTemplate.updateFirst(query, update, PactModel.class);
    }

    @Override
    public void deletePactById(Integer agencyId, String... ids) {
        if (ids != null) {
            for (String id : ids) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(id));
//                query.addCriteria(Criteria.where("agencyId").is(agencyId));
                mongoTemplate.remove(query, PactModel.class);
            }
        }
	    log.info("用户：{}删除了协议：{}",agencyId,ids);
    }

    @Override
    public Page<PactVo> findPactPage(PactModel param, Integer index, Integer pageSize) {
        List<PactVo> list = new ArrayList<>();
        Query dbQuery = createDbQuery(param);

        long count = mongoTemplate.count(dbQuery, PactModel.class);
        if (count > 0) {


            dbQuery.skip(index);// skip相当于从那条记录开始
            dbQuery.limit(pageSize);// 从skip开始,取多少条记录
            dbQuery.with(new Sort(Sort.Direction.DESC, "createTime"));
            list = mongoTemplate.find(dbQuery, PactVo.class);
        }
        list.forEach(e -> {
            if (e.getAgencyId() != null){
                e.setAgencyName(comAgencyService.findAgencyById(e.getAgencyId()).getAgencyName());
            }
        });
        return new Page<>(index, pageSize, (int) count, list);
    }

    private Query createDbQuery(PactModel param) {
        Query dbQuery = new Query();
        if (StringUtils.isNoneBlank(param.getTitle())) {
            dbQuery.addCriteria(Criteria.where("title").regex(".*?" + param.getTitle() + ".*"));
        }
        if (param.getAgencyId() != null) {
            dbQuery.addCriteria(Criteria.where("agencyId").is(param.getAgencyId()));
        }

        if (StringUtils.isNoneBlank(param.getCode())) {
            dbQuery.addCriteria(Criteria.where("code").is(param.getCode()));
        }
        if (param.getType() != null) {
            dbQuery.addCriteria(Criteria.where("type").is(param.getType()));

        }
        return dbQuery;
    }

    @Override
    public List<PactModel> findPactList(PactModel param) {
        Query dbQuery = createDbQuery(param);
        return mongoTemplate.find(dbQuery, PactModel.class);
    }

}
