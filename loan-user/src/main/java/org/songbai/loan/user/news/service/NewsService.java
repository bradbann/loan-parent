package org.songbai.loan.user.news.service;

import org.songbai.loan.model.news.NewsModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public interface NewsService {
	List<NewsModel> findNewsList(NewsModel newsModel, Locale locale, Integer offset, Integer size);

	NewsModel findNewsById(String id);
}
