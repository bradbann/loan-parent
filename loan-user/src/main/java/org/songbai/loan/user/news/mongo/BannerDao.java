package org.songbai.loan.user.news.mongo;


import org.songbai.loan.model.news.BannerModel;

import java.util.List;

/**
 * Created by xuesong on 14:54 2018/02/08
 */
public interface BannerDao {

	public List<BannerModel> findBannerList(Integer limit, Integer scope, Integer agencyId,Integer vestId);

	void updateClicks(Long id);

	BannerModel findBannerById(String id);

}
