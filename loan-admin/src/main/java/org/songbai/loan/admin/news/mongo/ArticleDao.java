/*
 * Copyright (c) 2016 Srs Group - 版权所有
 *
 * This software is the confidential and proprietary information of
 * Strong Group. You shall not disclose such confidential information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with www.srs.cn.
 */
package org.songbai.loan.admin.news.mongo;

import java.util.List;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.ArticleVo;
import org.songbai.loan.model.news.ArticleModel;


public interface ArticleDao {

    void saveArticle(ArticleModel article);

    void updateArticleById(ArticleModel article);

    ArticleModel queryArticleById(String id);

    ArticleModel queryArticle(String categoryCode, String categoryType);

    void deleteArticleById(String id);

    void batchDeleteArticle(List<String> list);

    Page<ArticleVo> queryArticleList(ArticleModel article, Integer index, Integer size);

    List<ArticleModel> findArticleListByIds(String idsStr);

}
