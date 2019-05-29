package org.songbai.loan.admin.news.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.news.mongo.BannerDao;
import org.songbai.loan.admin.news.service.BannerService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.news.NewsConst;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.news.BannerModel;
import org.songbai.loan.model.sms.PushGroupModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class BannerServiceImpl implements BannerService {
    private Logger logger = LoggerFactory.getLogger(BannerService.class);

    @Autowired
    private BannerDao bannerDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComAgencyService comAgencyService;

    @Override
    public Page<BannerModel> findBannerByPage(BannerModel bannerModel, Integer page, Integer pageSize, Integer agencyId) {

        Integer offset = page * pageSize;
        List<BannerModel> list = bannerDao.findByPage(bannerModel, offset, pageSize);
        list.forEach(e -> {
            if (e.getAgencyId() != null){
                e.setAgencyName(comAgencyService.findAgencyById(e.getAgencyId()).getAgencyName());
            }
        });
        Integer rows = bannerDao.findRows(bannerModel);
        Page<BannerModel> newsPage = new Page<>(page, pageSize, rows);
        newsPage.setData(list);
        return newsPage;
    }

    @Override
    public BannerModel findBanner(BannerModel bannerModel) {
        return bannerDao.loadBannerModel(bannerModel);
    }

    @Override
    public void saveBanner(BannerModel bannerModel) {
        if (StringUtil.isEmpty(bannerModel.getTitle())) {
            throw new ResolveMsgException("common.param.notnull", "title");
        }

        if (bannerModel.getJumpType() == null) {
            bannerModel.setJumpType(NewsConst.JumpType.H5.value);
        }

        if (bannerModel.getIndex() == null) {
            bannerModel.setIndex(0);
        }

        Date year = DateUtils.truncate(new Date(), Calendar.YEAR);

        if (bannerModel.getShowStartTime() == null) {
            //开始时间没有设置， 表示就是从今年开始，
            bannerModel.setShowStartTime(year);
        }
        if (bannerModel.getShowEndTime() == null) {
            //结束时间没有设置， 表示就是100年后，
            bannerModel.setShowEndTime(DateUtils.addYears(year, 100));
        }

        bannerModel.setStatus(CommonConst.STATUS_VALID);
        bannerDao.save(bannerModel);

    }


    @Override
    public boolean updateBanner(BannerModel bannerModel, AdminUserModel userModel) {

        BannerModel oldModel = bannerDao.loadBannerModel(bannerModel);
        if (oldModel == null) {
            return false;
        }

        Date year = DateUtils.truncate(new Date(), Calendar.YEAR);
        if (bannerModel.getShowStartTime() == null) {
            //开始时间没有设置， 表示就是从今年开始，
            bannerModel.setShowStartTime(year);
        }
        if (bannerModel.getShowEndTime() == null) {
            //结束时间没有设置， 表示就是100年后，
            bannerModel.setShowEndTime(DateUtils.addYears(year, 100));
        }

        bannerDao.updateBannerById(bannerModel);
        return true;
    }

    private void removeList(BannerModel bannerModel) {
//        List<ExchangeAdminModel> list = comExchangeService.findExchangeList(bannerModel.getAgencyId(), bannerModel.getIncludeAgency());
//        for (ExchangeAdminModel exchangeAdminModel : list) {
//            BannerModel param = new BannerModel();
//            param.setAgencyId(exchangeAdminModel.getAgencyId());
//            param.setUuid(bannerModel.getUuid());
//            bannerDao.removeList(param);
//        }
    }


    @Override
    public boolean deleteBanner(String ids, Integer agencyId) {
        List<BannerModel> list = bannerDao.findListByIds(ids);
        list.forEach(model -> {
            bannerDao.delete(model.getId());
        });
        return true;
    }

    @Override
    public void updateBannerStatus(BannerModel bannerModel) {
        if (bannerModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }
        if (bannerModel.getStatus() == null) {
            throw new ResolveMsgException("common.param.notnull", "status");
        }
        BannerModel oldModel = bannerDao.loadBannerModel(bannerModel);
        if (oldModel == null) {
            throw new ResolveMsgException("common.param.notnull", "oldModel");
        }

        bannerDao.updateBannerById(bannerModel);
    }

    @Override
    public void pushMsg(String id, Integer agencyId) {
        BannerModel bannerModel = new BannerModel();
        bannerModel.setId(id);
        if (agencyId != 0) {
            bannerModel.setAgencyId(agencyId);
        }
        BannerModel model = bannerDao.loadBannerModel(bannerModel);
        if (model == null) {
            return;
        }
        if (model.getVestlist().isEmpty()) {
            return;
        }
        if (model.getScopes().isEmpty()) {
            return;
        }
        List<Integer> scopes = new ArrayList<>();
        if (model.getScopes().contains(1)) {
            scopes.add(1);
        }
        if (model.getScopes().contains(2)) {
            scopes.add(2);
        }
        if (scopes.isEmpty()) {
            return;
        }

        PushGroupModel groupModel = new PushGroupModel();
        groupModel.setScopes(scopes);
        groupModel.setVestIds(new HashSet<>(model.getVestlist()));
        groupModel.setType(PushEnum.TYPE.NOTICE.value);
        groupModel.setMsg(model.getTitle());
        groupModel.setIsJump(CommonConst.NO);
        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_GROUP_MSG, groupModel);

        logger.info("推送公告,data={}", groupModel);
    }
}
