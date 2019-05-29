/**
 * @author huanglei
 * @date 2017年4月16日
 */

package org.songbai.loan.user.news.mongo.impl;

import org.songbai.loan.model.news.AgreementModel;
import org.songbai.loan.model.news.ArticleModel;
import org.songbai.loan.user.news.mongo.ArticleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ArticleDaoImpl implements ArticleDao {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public ArticleModel findArticleById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, ArticleModel.class);
	}

	@Override
	public List<ArticleModel> findArticleListByCode(String code, String lang, Long endTime, Integer size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryCode").is(code));
		if (lang != null) {
			query.addCriteria(Criteria.where("lang").is(lang));
		}
		if (endTime != null && endTime > 0) {
			query.addCriteria(Criteria.where("createTime").lt(new Date(endTime)));
		}

		query.with(new Sort(Sort.Direction.ASC, "index").and(new Sort(Sort.Direction.DESC, "createTime")));

		query.limit(size);

		return mongoTemplate.find(query, ArticleModel.class);
	}

	@Override
	public List<ArticleModel> findSimpleArticleListByCode(String code, Long endTime, Integer size, Integer agencyId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("categoryCode").is(code));


		if (agencyId != null) {
			query.addCriteria(Criteria.where("agencyId").is(agencyId));
		}

		query.fields().exclude("content");

		if (endTime != null && endTime > 0) {
			query.addCriteria(Criteria.where("createTime").lt(new Date(endTime)));
		}

		query.with(new Sort(Sort.Direction.ASC, "index").and(new Sort(Sort.Direction.DESC, "createTime")));

		query.limit(size);

		return mongoTemplate.find(query, ArticleModel.class);
	}


	@Override
	public boolean containArticleLang(String code, String lang) {


		Query query = new Query();

		query.addCriteria(Criteria.where("categoryCode").is(code));
		query.addCriteria(Criteria.where("lang").is(lang));

		return mongoTemplate.count(query, ArticleModel.class) > 0;
	}

	@Override
	public List<ArticleModel> findArticleByCode(String code) {

		Query query = new Query();
		query.addCriteria(Criteria.where("categoryCode").is(code));
		return mongoTemplate.find(query, ArticleModel.class);
	}

	@Override
	public AgreementModel findAgreementModel(String code, Integer agencyId) {
		Query query = new Query();

		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("agencyId").is(agencyId));
		return mongoTemplate.findOne(query, AgreementModel.class);
	}
}
