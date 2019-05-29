package org.songbai.loan.user.news.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.model.news.BannerModel;
import org.songbai.loan.user.news.mongo.BannerDao;
import org.songbai.loan.user.news.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuesong on 14:51 2018/02/08
 */
@Service
public class BannerServiceImpl implements BannerService {

	@Autowired
	private BannerDao bannerDao;

	@Override
	public List<BannerModel> findBannerList(Integer limit, Integer scope, String version, Integer agencyId,Integer vestId) {

		List<BannerModel> bannerList = bannerDao.findBannerList(limit, scope, agencyId,vestId);
		if (CollectionUtils.isEmpty(bannerList)) {
			return new ArrayList<>();
		}
		List<BannerModel> resultList = new ArrayList<>();
		for (BannerModel model : bannerList) {
			if (StringUtils.isNotEmpty(version)
					&& (StringUtils.isNotBlank(model.getIncludeVersion()) || StringUtils.isNotBlank(model.getExcludeVersion()))) {
				// 这个banner 只能在特定版本显示。
				if (StringUtils.isNotBlank(model.getIncludeVersion())
						&& checkVersion(version, model.getIncludeVersion())) {
					resultList.add(model);
					continue;
				}

				if (StringUtils.isNotBlank(model.getExcludeVersion())
						&& !checkVersion(version, model.getExcludeVersion())) {
					resultList.add(model);
					continue;
				}

			} else {
				resultList.add(model);
			}
		}
		return resultList;
	}

	@Override
	public void click(Long id) {
		bannerDao.updateClicks(id);
	}

	@Override
	public BannerModel findBannerById(String id) {

		return bannerDao.findBannerById(id);
	}


	private boolean checkVersion(String bannerVersion, String version) {
		String[] versions = null;
		boolean flag = false;
		if (version.indexOf(",") != -1) {
			versions = StringUtil.tokenizeToStringArray(version);
			for (String ver : versions) {
				if (bannerVersion.equals(ver)) {
					flag = true;
					break;
				}
			}
		} else {
			if (bannerVersion.equals(version)) {
				flag = true;
			}
		}
		return flag;
	}

}
