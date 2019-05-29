package org.songbai.loan.admin.version.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.push.dao.PushSenderDao;
import org.songbai.loan.admin.version.dao.AdminVestDao;
import org.songbai.loan.admin.version.model.vo.AppVestVO;
import org.songbai.loan.admin.version.service.AdminVestService;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.news.PactModel;
import org.songbai.loan.model.sms.PushSenderModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mr.czh on 2017/11/20.
 */
@Service
public class AdminVestServiceImpl implements AdminVestService {
    @Autowired
    private AdminVestDao adminVestDao;
    @Autowired
    private PushSenderDao pushSenderDao;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<AppVestVO> findByPage(AppVestModel vestModel, Integer page, Integer pageSize) {
        Integer offset = page * pageSize;
        List<AppVestVO> list = new ArrayList<>();
        Integer rows = adminVestDao.queryRows(vestModel);
        if (rows > 0) {
            list = adminVestDao.findByPage(vestModel, offset, pageSize);
        }

        list.forEach(e -> {
            if (StringUtil.isNotEmpty(e.getPactId())) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(e.getPactId()));
                PactModel one = mongoTemplate.findOne(query, PactModel.class);
                if (one != null) {
                    e.setPactName(one.getTitle());
                }
            }
            if (e.getAgencyId() != null){
                e.setAgencyName(comAgencyService.findAgencyById(e.getAgencyId()).getAgencyName());
            }
        });

        return new Page<>(page, pageSize, rows, list);
    }

    @Override
    public void saveVest(AppVestModel vestModel) {
//        AppVestModel params = new AppVestModel();
//        params.setAgencyId(vestModel.getAgencyId());
//        params.setPlatform(vestModel.getPlatform());
//        params.setVersion(vestModel.getVersion());
//        AppVestModel checkModel = adminVestDao.selectOne(params);
//        if (checkModel != null) {
//            throw new BusinessException(703, "该代理商马甲已经存在");
//        }
        String vestCode = createVestCode(vestModel.getAgencyId());
        vestModel.setVestCode(vestCode);

        PushSenderModel p = pushSenderDao.findStartPushSenderByIdAndAgencyId(vestModel.getPushSenderId(), vestModel.getAgencyId());
        if (p == null) {
            throw new BusinessException(AdminRespCode.PUSH_SENDER_NOT_FIND);
        }


        adminVestDao.insert(vestModel);
    }

    @Override
    public void updateVest(AppVestModel vestModel) {

        AppVestModel oldModel = adminVestDao.selectById(vestModel.getId());
        if (oldModel == null) {
            throw new BusinessException(703, "该数据不存在");
        }
        Assert.notNull(vestModel.getGroupId(), "groupId不能为空");
        vestModel.setVestCode(oldModel.getVestCode());
        PushSenderModel p = pushSenderDao.findStartPushSenderByIdAndAgencyId(vestModel.getPushSenderId(), oldModel.getAgencyId());
        if (p == null) {
            throw new BusinessException(AdminRespCode.PUSH_SENDER_NOT_FIND);
        }

//
//        AppVestModel param = new AppVestModel();
//        param.setAgencyId(vestModel.getAgencyId());
//        param.setPlatform(vestModel.getPlatform());
//        param.setVersion(vestModel.getVersion());
//        AppVestModel checkModel = adminVestDao.selectOne(param);
//        if (checkModel != null && !checkModel.getId().equals(vestModel.getId())) {
//            throw new BusinessException(703, "该代理商马甲已经存在");
//        }

        adminVestDao.updateById(vestModel);
    }

    @Override
    public void deleteVest(String[] idArr) {
        adminVestDao.deleteByIds(idArr);
    }

    @Override
    public List<AppVestModel> findVestList(AppVestModel model) {
        List<AppVestModel> list = adminVestDao.findVestList(model);
        if (model.getAgencyId() == null || model.getAgencyId() == 0) {
            list.forEach(e -> {
                if (e.getAgencyId() != null) {
                    AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agencyModel != null) {
                        e.setName(agencyModel.getAgencyName() + "的" + e.getName());
                    }
                }
            });
        }
        return list;
    }

    private String createVestCode(Integer agencyId) {
        String vestCode = OrderIdUtil.generateShortUuid();
        if (checkLandCode(vestCode, agencyId)) {
            createVestCode(agencyId);
        }
        return vestCode;
    }

    private boolean checkLandCode(String vestCode, Integer agencyId) {
        return adminVestDao.findVestByVestCode(vestCode, agencyId) != null;
    }

}
