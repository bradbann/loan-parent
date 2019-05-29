package org.songbai.loan.admin.news.mongo;

import java.util.List;

import org.songbai.loan.model.news.BannerModel;

public interface BannerDao {

    void save(BannerModel bannerModel);

    BannerModel loadBannerModel(BannerModel bannerModel);

    List<BannerModel> findByPage(BannerModel bannerModel, Integer offset, Integer pageSize);

    Integer findRows(BannerModel bannerModel);

    void delete(String ids);

    List<BannerModel> findListByIds(String ids);

    void updateBannerById(BannerModel bannerModel);
}
