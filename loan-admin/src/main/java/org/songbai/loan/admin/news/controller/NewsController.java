package org.songbai.loan.admin.news.controller;

import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.model.po.NewsVo;
import org.songbai.loan.admin.news.service.NewsService;
import org.songbai.loan.model.news.NewsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 公告中心
 *
 * @date 2018年10月31日 14:45:07
 * @description
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;
    @Autowired
    private AdminUserHelper adminUserHelper;

    @GetMapping(value = {"/findNewsByPage"})
    @ResponseBody
    public Response findNewsByPage(NewsModel newsModel, Integer page, Integer pageSize, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        if (newsModel == null) {
            newsModel = new NewsModel();
        }

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;

        if (userModel.getDataId() != 0) {
            newsModel.setAgencyId(userModel.getDataId());
        }

        Page<NewsVo> pageResult = newsService.findNewsByPage(newsModel, page, pageSize);
        return Response.success(pageResult);
    }


    @GetMapping(value = {"/findNews"})
    @ResponseBody
    public Response findNews(NewsModel newsModel) {

        if (newsModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        NewsModel result = newsService.findNews(newsModel);

        return Response.success(result);
    }


    @PostMapping(value = {"/saveNews"})
    @ResponseBody
    public Response saveNews(NewsModel newsModel, HttpServletRequest request) {
        Assert.notNull(newsModel, "参数不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        newsModel.setOperator(userModel.getUserAccount());
        if (userModel.getDataId() != 0) {
            newsModel.setAgencyId(userModel.getDataId());
        }
        newsService.saveNews(newsModel);
        return Response.success();
    }

    @RequestMapping(value = {"/updateNews"})
    @ResponseBody
    public Response updateNews(NewsModel newsModel, HttpServletRequest request) {
        Assert.notNull(newsModel, "参数不能为空");

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (userModel.getDataId() != 0) {
            newsModel.setAgencyId(userModel.getDataId());
        }
        newsModel.setOperator(userModel.getName());

        newsService.updateNews(newsModel, userModel.getDataId());
        return Response.success();
    }

    @RequestMapping(value = {"/deleteNews"})
    @ResponseBody
    public Response deleteNews(String ids, HttpServletRequest request) {
        Assert.notNull(ids, "参数不能为空");

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        newsService.deleteNews(ids, userModel.getDataId());

        return Response.success();
    }

    @PostMapping(value = {"/updateStatus"})
    @ResponseBody
    public Response updateStatus(NewsModel newsModel, HttpServletRequest request) {
        Assert.notNull(newsModel, "参数不能为空");

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        newsModel.setOperator(userModel.getName());

        newsService.updateNewsStatus(newsModel);
        return Response.success();
    }


    @PostMapping(value = {"/pushMsg"})
    public Response pushMsg(String id, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        newsService.pushMsg(id, agencyId);
        return Response.success();
    }


}
