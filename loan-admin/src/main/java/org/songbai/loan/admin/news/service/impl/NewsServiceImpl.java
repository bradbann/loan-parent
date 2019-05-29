package org.songbai.loan.admin.news.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.news.model.po.NewsVo;
import org.songbai.loan.admin.news.mongo.NewsDao;
import org.songbai.loan.admin.news.service.NewsService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.news.NewsModel;
import org.songbai.loan.model.sms.PushGroupModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NewsServiceImpl implements NewsService {
    private Logger logger = LoggerFactory.getLogger(NewsService.class);
    @Autowired
    private NewsDao newsDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComAgencyService comAgencyService;

    @Override
    public Page<NewsVo> findNewsByPage(NewsModel newsModel, Integer page, Integer pageSize) {
        Integer offset = page > 0 ? page * pageSize : 0;
        List<NewsVo> list = newsDao.findByPage(newsModel, offset, pageSize);
        list.forEach(e -> {
            if (e.getAgencyId() != null){
                e.setAgencyName(comAgencyService.findAgencyById(e.getAgencyId()).getAgencyName());
            }
        });
        Integer rows = newsDao.findRows(newsModel);
        Page<NewsVo> newsPage = new Page<>(page, pageSize, rows);
        newsPage.setData(list);
        return newsPage;
    }

    @Override
    public NewsModel findNews(NewsModel newsModel) {
        return newsDao.find(newsModel);
    }

    @Override
    public void saveNews(NewsModel newsModel) {

        if (newsModel.getType() != null && newsModel.getType() == 0) {
            throw new ResolveMsgException("common.param.notnull", "type");
        }
        if (StringUtil.isEmpty(newsModel.getTitle())) {
            throw new ResolveMsgException("common.param.notnull", "title");
        }

        if (newsModel.getFormat() == null) {
            throw new ResolveMsgException("common.param.notnull", "format");
        }

        if (newsModel.getType() == null) {
            throw new ResolveMsgException("common.param.notnull", "type");
        }


        if (newsModel.getIndex() == null) {
            newsModel.setIndex(0);
        }

        Date year = DateUtils.truncate(new Date(), Calendar.YEAR);

        if (newsModel.getShowStartTime() == null) {
            //开始时间没有设置， 表示就是从今年开始，
            newsModel.setShowStartTime(year);
        }
        if (newsModel.getShowEndTime() == null) {
            //结束时间没有设置， 表示就是100年后，
            newsModel.setShowEndTime(DateUtils.addYears(year, 100));
        }

        if (newsModel.getStatus() == null) {
            newsModel.setStatus(CommonConst.STATUS_VALID);
        }
        newsDao.save(newsModel);
    }

    @Override
    public void updateNews(NewsModel newsModel, Integer AgencyId) {

        if (newsModel == null || newsModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }

        NewsModel oldNews = newsDao.find(newsModel);

        if (!oldNews.getId().equals(newsModel.getId())) {
            throw new ResolveMsgException("common.param.repeat", "code");
        }

        Date year = DateUtils.truncate(new Date(), Calendar.YEAR);
        if (newsModel.getShowStartTime() == null) {
            //开始时间没有设置， 表示就是从今年开始，
            newsModel.setShowStartTime(year);
        }
        if (newsModel.getShowEndTime() == null) {
            //结束时间没有设置， 表示就是100年后，
            newsModel.setShowEndTime(DateUtils.addYears(year, 100));
        }

        newsDao.update(newsModel);
    }

    private void removeList(NewsModel newsModel) {
//        List<ExchangeAdminModel> list = comExchangeService.findExchangeList(newsModel.getAgencyId(), newsModel.getIncludeAgency());
//        for (ExchangeAdminModel exchangeAdminModel : list) {
//            NewsModel param = new NewsModel();
//            param.setAgencyId(exchangeAdminModel.getAgencyId());
//            param.setUuid(newsModel.getUuid());
//            newsDao.removeNews(param);
//        }
    }

    @Override
    public void deleteNews(String ids, Integer AgencyId) {
        List<NewsModel> list = newsDao.findListByIds(ids);
        list.forEach(model -> {
            newsDao.delete(model.getId());
        });

    }

    @Override
    public void updateNewsStatus(NewsModel newsModel) {
        if (newsModel.getId() == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }
        if (newsModel.getStatus() == null) {
            throw new ResolveMsgException("common.param.notnull", "status");
        }
        if (newsModel.getShowStartTime() == null) {
            throw new ResolveMsgException("common.param.notnull", "showStartTime");
        }
        NewsModel oldModel = newsDao.find(newsModel);
        if (oldModel == null) {
            throw new ResolveMsgException("common.param.notnull", "oldModel");
        }

        newsDao.update(newsModel);
    }

    @Override
    public void pushMsg(String id, Integer agencyId) {
        NewsModel newsModel = new NewsModel();
        newsModel.setId(id);
        if (agencyId != 0) {
            newsModel.setAgencyId(agencyId);
        }

        NewsModel model = newsDao.find(newsModel);
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
        groupModel.setMsg("【公告】" + model.getTitle());
        groupModel.setIsJump(CommonConst.NO);
        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_GROUP_MSG, groupModel);

        logger.info("推送公告,data={}", groupModel);
    }
}
