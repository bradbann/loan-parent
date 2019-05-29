/*
 * Copyright (c) 2016 Srs Group - 版权所有
 *
 * This software is the confidential and proprietary information of
 * Strong Group. You shall not disclose such confidential information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with www.srs.cn.
 */
package org.songbai.loan.admin.news.mongo.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.ArticleVo;
import org.songbai.loan.admin.news.mongo.ArticleDao;
import org.songbai.loan.model.news.ArticleModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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


/**
 * 描述:
 *
 * @author C.C
 * @created 2016年11月1日 下午5:07:19
 * @since v1.0.0
 */
@Repository
public class ArticleDaoImpl implements ArticleDao {

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private ComAgencyService comAgencyService;

	@PostConstruct
	public void init() {
		mongoTemplate.indexOps(ArticleModel.class).ensureIndex(new Index("categoryCode", Sort.Direction.DESC));
		mongoTemplate.indexOps(ArticleModel.class).ensureIndex(new Index("createTime", Sort.Direction.DESC));
		mongoTemplate.indexOps(ArticleModel.class).ensureIndex(new Index("status", Sort.Direction.DESC));
	}


	@Override
	public void saveArticle(ArticleModel article) {
		Date date = new Date();
		article.setCreateTime(date);
		article.setModifyTime(date);
		mongoTemplate.save(article);
	}

	@Override
	public void updateArticleById(ArticleModel article) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(article.getId()));

		Update update = wrapperUpdate(article);

		mongoTemplate.updateFirst(query, update, ArticleModel.class);
	}


	@Override
	public ArticleModel queryArticleById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, ArticleModel.class);
	}

	@Override
	public ArticleModel queryArticle(String categoryCode, String categoryType) {
		Query query = new Query();
		if (null != categoryType) {
			query.addCriteria(Criteria.where("categoryType").is(categoryType));
		}
		if (null != categoryCode) {
			query.addCriteria(Criteria.where("categoryCode").is(categoryCode));
		}
		return mongoTemplate.findOne(query, ArticleModel.class);
	}

	@Override
	public void deleteArticleById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		mongoTemplate.remove(query, ArticleModel.class);
	}

	/**
	 * 根据id删除文章
	 * modify by zz 2017年6月14日
	 */
	@Override
	public void batchDeleteArticle(List<String> idList) {
		for (String id : idList) {
			deleteArticleById(id);
		}
	}

	@Override
	public Page<ArticleVo> queryArticleList(ArticleModel article, Integer index, Integer size) {

		List<ArticleVo> list = new ArrayList<>();
		Query dbQuery = new Query();

		if (StringUtils.isNoneBlank(article.getTitle())) {
			dbQuery.addCriteria(Criteria.where("title").regex(".*?" + article.getTitle() + ".*"));
		}
		if (article.getAgencyId() != null) {
			dbQuery.addCriteria(Criteria.where("agencyId").is(article.getAgencyId()));
		}

		if (StringUtils.isNoneBlank(article.getCategoryCode())) {
			dbQuery.addCriteria(Criteria.where("categoryCode").is(article.getCategoryCode()));
		}

		if (article.getLabels() != null) {
			dbQuery.addCriteria(Criteria.where("labels").all(article.getLabels()));
		}

		if (StringUtils.isNotEmpty(article.getSources())) {
			dbQuery.addCriteria(Criteria.where("sources").is(article.getSources()));
		}

		long count = mongoTemplate.count(dbQuery, ArticleModel.class);
		if (count > 0) {
			dbQuery.skip(index);// skip相当于从那条记录开始
			dbQuery.limit(size);// 从skip开始,取多少条记录
			dbQuery.with(new Sort(Direction.DESC, "createTime"));
			list = mongoTemplate.find(dbQuery, ArticleVo.class);
		}
		list.forEach(e -> {
            if (e.getAgencyId() != null){
            	e.setAgencyName(comAgencyService.findAgencyById(e.getAgencyId()).getAgencyName());
            }
		});
		Page<ArticleVo> page = new Page<>(index, size, (int) count);

		page.setData(list);
		return page;
	}

	@Override
	public List<ArticleModel> findArticleListByIds(String idsStr) {
		Query query = Query.query(Criteria.where("id").in(Arrays.asList(idsStr.split(","))));
		return mongoTemplate.find(query, ArticleModel.class);
	}

	private Update wrapperUpdate(ArticleModel article) {
		Update update = new Update();
		if (StringUtils.isNotEmpty(article.getCategoryCode())) {
			update.set("categoryCode", article.getCategoryCode());
		}
		if (null != article.getCategoryType()) {
			update.set("categoryType", article.getCategoryType());
		}

		if (StringUtils.isNotEmpty(article.getPublishUser())) {
			update.set("publishUser", article.getPublishUser());
		}
		if (StringUtils.isNotEmpty(article.getTitle())) {
			update.set("title", article.getTitle());
		}
		if (StringUtils.isNotEmpty(article.getSubTitle())) {
			update.set("subTitle", article.getSubTitle());
		}
		if (article.getImgs() != null) {
			update.set("imgs", article.getImgs());
		}
		if (article.getFormat() != null) {
			update.set("format", article.getFormat());
		}
		if (StringUtils.isNotEmpty(article.getContent())) {
			update.set("content", article.getContent());
		}
		if (StringUtils.isNotEmpty(article.getRemark())) {
			update.set("remark", article.getRemark());
		}
		if (article.getFormat() != null) {
			update.set("format", article.getFormat());
		}
		if (StringUtils.isNotEmpty(article.getSources())) {
			update.set("sources", article.getSources());
		}
		if (null != article.getClicks()) {
			update.set("clicks", article.getClicks());
		}
		if (null != article.getLabels()) {
			update.set("labels", article.getLabels());
		}
		if (null != article.getIndex()) {
			update.set("index", article.getIndex());
		}

		update.set("operator", article.getOperator());
		update.set("modifyTime", new Date());

		return update;
	}


}
