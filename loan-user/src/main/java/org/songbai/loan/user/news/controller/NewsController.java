package org.songbai.loan.user.news.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.news.NewsModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 公告
 *
 * @description
 */
@RestController
@RequestMapping("/news")
@LimitLess
public class NewsController {
	private Logger logger = LoggerFactory.getLogger(NewsController.class);

	@Autowired
	private NewsService newsService;
	@Autowired
	SpringProperties properties;
	@Autowired
	private ComAgencyService comAgencyService;


	@RequestMapping("/findNewsList")
	public Response findNewsList(NewsModel newsModel, Integer offset, Integer page, Integer size, HttpServletRequest request) {
		offset = offset == null ? 0 : offset;
		size = size == null ? Page.DEFAULE_PAGESIZE : size;

		if (page != null && page > 0) {
			offset = page * size;
		}

		Integer agencyId = comAgencyService.findAgencyIdByRequest(request);
		String vestCode = PlatformKit.parseChannel(request);

		Integer vestId = comAgencyService.findVestIdByVestCode(agencyId,vestCode);

		newsModel.setStatus(CommonConst.STATUS_VALID);
		newsModel.setAgencyId(agencyId);
		newsModel.setVestlist(Arrays.asList(vestId));

		return Response.success(newsService.findNewsList(newsModel, LocaleKit.getLocale(), offset, size));
	}

	@RequestMapping("/detail/{id}")
	public Response findNews(@PathVariable("id") String id) {


		return Response.success(newsService.findNewsById(id));
	}
}
