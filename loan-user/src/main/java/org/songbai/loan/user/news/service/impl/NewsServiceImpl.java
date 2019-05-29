package org.songbai.loan.user.news.service.impl;

import org.songbai.loan.model.news.NewsModel;
import org.songbai.loan.user.news.mongo.NewsDao;
import org.songbai.loan.user.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	private NewsDao newsDao;


	@Override
	public List<NewsModel> findNewsList(NewsModel newsModel, Locale locale, Integer offset, Integer size) {

		return newsDao.findList(newsModel, offset, size);
	}

	@Override
	public NewsModel findNewsById(String id) {

		return newsDao.findById(id);
	}

}
