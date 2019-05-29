package org.songbai.loan.user.news.mongo.impl;


import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.news.BannerModel;
import org.songbai.loan.user.news.mongo.BannerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by xuesong on 14:54 2018/02/08
 */
@Repository
public class BannerDaoImpl implements BannerDao {
	@Autowired
	MongoTemplate mongoTemplate;

	public static final Integer SHOW = 1;

	@Override
	public List<BannerModel> findBannerList(Integer limit, Integer scope, Integer agencyId,Integer vestId) {
		Query query = Query.query(Criteria.where("status").is(CommonConst.STATUS_VALID));
		Date date = new Date();
		query.addCriteria(Criteria.where("showStartTime").lt(date).and("showEndTime").gt(date));


		if (scope != null) {
			query.addCriteria(Criteria.where("scopes").all(scope));
		}
		if (agencyId != null) {
			query.addCriteria(Criteria.where("agencyId").is(agencyId));
		}
		if(vestId !=null){
			query.addCriteria(Criteria.where("vestlist").all(vestId));
		}

		query.with(new Sort(Sort.Direction.ASC, "index").and(new Sort(Sort.Direction.DESC, "updateTime")));
		query.limit(limit);
		return mongoTemplate.find(query, BannerModel.class);
	}

	@Override
	public void updateClicks(Long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.inc("clicks", 1);
		mongoTemplate.upsert(query, update, BannerModel.class);
	}

	@Override
	public BannerModel findBannerById(String id) {

		return mongoTemplate.findById(id, BannerModel.class);
	}
}
