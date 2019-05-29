package org.songbai.loan.admin.news.mongo;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.news.model.po.NewsVo;
import org.songbai.loan.model.news.NewsModel;

import java.util.List;

public interface NewsDao {
    List<NewsVo> findByPage(@Param("vo") NewsModel newsModel, @Param("offset") Integer offset,
                            @Param("pageSize") Integer pageSize);

    Integer findRows(@Param("vo") NewsModel newsModel);

    NewsModel find(NewsModel newsModel);

    void save(NewsModel newsModel);

    void update(NewsModel newsModel);

    void delete(String ids);

    List<NewsModel> findListByIds(String ids);


}
