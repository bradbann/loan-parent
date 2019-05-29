package org.songbai.loan.admin.news.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.model.po.ArticleVo;
import org.songbai.loan.admin.news.service.ArticleService;
import org.songbai.loan.model.news.ArticleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 帮助中心内容、帮助模块
 *
 * @date 2018年10月31日 14:48:09
 * @description
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private AdminUserHelper adminUserHelper;


    /**
     * 帮助管理
     */
    @RequestMapping("/articleList")
    public Response articleList(ArticleModel article, Integer page, Integer pageSize, HttpServletRequest request) {
        AdminUserModel admin = adminUserHelper.getAdminUser(request);

        if (null == page) {
            page = 0;
        }
        if (null == pageSize) {
            pageSize = 20;
        }

        Page<ArticleVo> pageView = articleService.findArticleList(article, page, pageSize, admin.getDataId());
        return Response.success(pageView);
    }


    /**
     * 新增帮助
     */
    @RequestMapping("/addArticle")
    public Response addArticle(ArticleModel article, HttpServletRequest request) {
        AdminUserModel admin = adminUserHelper.getAdminUser(request);
        checkArticle(article);

        if (admin.getDataId() != 0) {
            article.setAgencyId(admin.getDataId());
        }

        article.setOperator(admin.getName());
        articleService.addArticle(article);
        return Response.success();
    }

    /**
     * 修改帮助
     */
    @RequestMapping("/updateArticle")
    public Response updateArticle(ArticleModel article, HttpServletRequest request) {
        if (article == null || article.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        AdminUserModel admin = adminUserHelper.getAdminUser(request);
        checkArticle(article);

        article.setOperator(admin.getName());
        articleService.updateArticle(article, admin.getDataId());
        return Response.success();
    }

    @RequestMapping("/batchDeleteArticle")
    public Response batchDeleteArticle(String idsStr, HttpServletRequest request) {
        AdminUserModel admin = adminUserHelper.getAdminUser(request);


        String[] idArr = idsStr.split(",");
        List<String> idList = Arrays.asList(idArr);
        articleService.batchDeleteArticle(idsStr, admin.getDataId());
        return Response.success();
    }


    @RequestMapping("/articleDetail")
    public Response articleDetail(String id) {
        return Response.success(articleService.findArticleById(id));
    }

    /**
     * 描述:验证各字段是否必填合法
     */
    private void checkArticle(ArticleModel article) {
        if (StringUtils.isBlank(article.getTitle()) || article.getTitle().length() > 30) {
            throw new ResolveMsgException(RespCode.PARAM_STR_TOO_LONG, "common.param.str.length.max", "title", 30);
        }

        if (StringUtils.isBlank(article.getContent())) {
            throw new ResolveMsgException(RespCode.PARAM_STR_TOO_LONG, "common.param.notnull", "context");
        }
        if (article.getFormat() == null) {
            throw new ResolveMsgException(RespCode.PARAM_STR_TOO_LONG, "common.param.notnull", "Format");
        }

        if (article.getCategoryType() == null) {
            throw new ResolveMsgException(RespCode.PARAM_STR_TOO_LONG, "common.param.notnull", "categoryType");
        }
    }
}
