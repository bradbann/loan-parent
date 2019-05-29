package org.songbai.loan.user.news.controller;

import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.model.news.BannerModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.user.news.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * banner
 * Created by xuesong on 14:43 2018/02/08
 */
@RequestMapping("/banner")
@RestController
@LimitLess
public class BannerController {
	private Logger logger = LoggerFactory.getLogger(BannerController.class);

	public static final Integer LIMIT = 10;

	@Autowired
	private BannerService bannerService;
	@Autowired
	SpringProperties properties;
	@Autowired
	private ComAgencyService comAgencyService;

	/**
	 * 查询可见的Banner（最多为limit条）
	 *
	 * @return
	 */
	@GetMapping(value = "/findBannerList")
	@LimitLess
	public Response findBannerList(HttpServletRequest request, Integer platform) {
		//ios和所有范围
		String version = request.getHeader(PlatformKit.VERSION);

		if (platform == null) {
			platform = PlatformKit.parsePlatform(request).value;
		}
		//取请求域名
		Integer agencyId = comAgencyService.findAgencyIdByRequest(request);

		String vestCode = PlatformKit.parseChannel(request);

		Integer vestId = comAgencyService.findVestIdByVestCode(agencyId,vestCode);


		List<BannerModel> list = bannerService.findBannerList(LIMIT, platform, version, agencyId,vestId);
		return Response.success(list);
	}

	/**
	 * @return
	 */
	@GetMapping(value = "/findBannerById")
	@LimitLess
	public Response findBannerById(String id) {

		Asserts.notEmpty(id, LocaleKit.get("common.param.notnull", "id"));

		return Response.success(bannerService.findBannerById(id));
	}

	/**
	 * banner点击次数
	 */
	@PutMapping(value = "/click")
	@LimitLess
	public void findBannerById(Long id) {
		Assert.notNull(id, "获取不到需要操作的条目");
		//更新点击数
		bannerService.click(id);
	}
}
