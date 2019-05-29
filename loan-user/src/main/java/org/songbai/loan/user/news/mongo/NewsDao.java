package org.songbai.loan.user.news.mongo;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.news.NewsModel;

import java.util.List;

public interface NewsDao {
	public List<NewsModel> findList(@Param("model") NewsModel newsModel, @Param("offset") Integer offset,
	                                @Param("size") Integer size);

	public NewsModel findById(String id);

}
