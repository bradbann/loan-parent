package org.songbai.loan.user.news.service.impl;

import org.songbai.loan.model.news.AgreementModel;
import org.songbai.loan.model.news.ArticleModel;
import org.songbai.loan.user.news.mongo.ArticleDao;
import org.songbai.loan.user.news.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	ArticleDao articleDao;


	@Value("${config.lang:en}")
	private String defaultLang;

	@Override
	public ArticleModel findArticleById(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return articleDao.findArticleById(id);
	}

	@Override
	public List<ArticleModel> findArticleListByCode(String code, String locale, Long endTime, Integer size) {

		return articleDao.findArticleListByCode(code, locale, endTime, size);
	}

	@Override
	public List<ArticleModel> findSimpleArticleListByCode(String code, Long endTime, Integer size, Integer agencyId) {

		return articleDao.findSimpleArticleListByCode(code, endTime, size, agencyId);
	}


	@Override
	public boolean containArticleLang(String code, String locale) {

		return articleDao.containArticleLang(code, locale);
	}

	@Override
	public ArticleModel findArticleByCode(String code) {

		List<ArticleModel> list = articleDao.findArticleByCode(code);
		if (CollectionUtils.isEmpty(list)) return null;
		return list.get(0);
	}


	@Override
	public AgreementModel findAgreementModel(String code, Integer agencyId) {

		return articleDao.findAgreementModel(code, agencyId);
	}
}
