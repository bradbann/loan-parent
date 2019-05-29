package org.songbai.loan.user.news.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.news.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 帮助中心
 *
 * @description
 */
@RestController
@RequestMapping("/article")
@LimitLess
public class ArticleController {
	private Logger logger = LoggerFactory.getLogger(ArticleController.class);

	@Autowired
	private ArticleService articleService;
	@Autowired
	SpringProperties properties;
	@Autowired
	private ComAgencyService comAgencyService;


	/**
	 * 显示文章详情
	 */
	@RequestMapping(value = "/articleDetail")
	public Response articleDetailById(String id) {

		if (StringUtils.isEmpty(id)) {
			throw new BusinessException(LocaleKit.get("common.param.notnull", "id"));
		}

		return Response.success(articleService.findArticleById(id));
	}


	/**
	 * 更具code 获取指定文章， 一般这个文章只有一个
	 */
	@GetMapping("detail/{code}")
	public Response articleDetail(@PathVariable("code") String code) {


		return Response.success(articleService.findArticleByCode(code));
	}


	@GetMapping("list/{code}")
	public Response articleDetail(@PathVariable("code") String code, Long endTime, Integer size,
	                              HttpServletRequest request) {

		size = size == null ? Page.DEFAULE_PAGESIZE : size;

		//取请求域名
		Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

		return Response.success(articleService.findSimpleArticleListByCode(code, endTime, size, agencyId));
	}


	@GetMapping("getAgreement")
	public Response getAgreement(String code, HttpServletRequest request) {

		if (StringUtils.isEmpty(code)) {
			throw new BusinessException(LocaleKit.get("common.param.notnull", "code"));
		}

		//取请求域名
		Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

		return Response.success(articleService.findAgreementModel(code, agencyId));
	}


}