/*
 * Copyright (c) 2016 Srs Group - 版权所有
 *
 * This software is the confidential and proprietary information of
 * Strong Group. You shall not disclose such confidential information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with www.srs.cn.
 */
package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.ArticleVo;
import org.songbai.loan.model.news.ArticleModel;
import org.springframework.stereotype.Component;

/**
 * 描述:
 */
@Component
public interface ArticleService {


    void addArticle(ArticleModel article);

    /**
     * 描述:根据ID更新文章
     */
    void updateArticle(ArticleModel article, Integer agencyId);

    /**
     * 描述:根据ID查询文章
     *
     * @param id
     * @return ArticleModel
     */
    ArticleModel findArticleById(String id);

    /**
     * 描述:多条件查询文章
     */
    ArticleModel findArticle(String categoryCode, String categoryType);


    /**
     * 描述:多条件查询文章列表
     *
     * @return Page<ArticleModel>
     */
    Page<ArticleVo> findArticleList(ArticleModel article, Integer page, Integer pageSize, Integer agencyId);


    /**
     * 批量删除文章
     */
    void batchDeleteArticle(String idsStr, Integer agencyId);

}
