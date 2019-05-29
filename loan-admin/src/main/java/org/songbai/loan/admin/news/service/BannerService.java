package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.model.news.BannerModel;

public interface BannerService {
    Page<BannerModel> findBannerByPage(BannerModel newsModel, Integer page, Integer pageSize, Integer agencyId);

    BannerModel findBanner(BannerModel newsModel);

    void saveBanner(BannerModel newsModel);

    boolean updateBanner(BannerModel newsModel, AdminUserModel userModel);


    boolean deleteBanner(String ids, Integer agencyId);

    void updateBannerStatus(BannerModel bannerModel);

    void pushMsg(String id, Integer agencyId);

}
