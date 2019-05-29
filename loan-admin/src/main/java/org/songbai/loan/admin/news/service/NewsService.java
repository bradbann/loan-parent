package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.NewsVo;
import org.songbai.loan.model.news.NewsModel;
import org.springframework.stereotype.Component;

@Component
public interface NewsService {


    Page<NewsVo> findNewsByPage(NewsModel newsModel, Integer page, Integer pageSize);

    NewsModel findNews(NewsModel newsModel);

    void saveNews(NewsModel newsModel);

    void updateNews(NewsModel newsModel, Integer agencyId);

    void deleteNews(String ids, Integer agencyId);

    void updateNewsStatus(NewsModel newsModel);


    void pushMsg(String id,Integer agencyId);

}
