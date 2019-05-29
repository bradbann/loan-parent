package org.songbai.loan.user.news.service;


import org.songbai.loan.model.news.BannerModel;

import java.util.List;

/**
 * Created by xuesong on 14:51 2018/02/08
 */
public interface BannerService {
//    List<BannerModel> findBannerList(Integer limit, List<Integer> scope, Integer type, String version);

	List<BannerModel> findBannerList(Integer limit, Integer scope, String version, Integer agencyId,Integer vestId);

	void click(Long id);

	BannerModel findBannerById(String id);

}
