package org.songbai.loan.admin.activity.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.activity.dao.ActivityDao;
import org.songbai.loan.admin.activity.model.vo.ActivityModelVO;
import org.songbai.loan.admin.activity.service.ActivityService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.ActivityRespCode;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.activity.ActivityModel;
import org.songbai.loan.model.sms.PushGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Author: qmw
 * Date: 2018/12/17 1:32 PM
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
    @Override
    public void addActivity(ActivityModel model) {
        EntityWrapper<ActivityModel> codeEw = new EntityWrapper<>();
        codeEw.eq("code", model.getCode());
        codeEw.eq("agency_id", model.getAgencyId());
        codeEw.eq("deleted", CommonConst.DELETED_NO);
        Integer count = activityDao.selectCount(codeEw);
        if (count > 0) {
            throw new BusinessException(ActivityRespCode.ACTIVITY_CODE_EXIST);
        }

        if (model.getStatus() == CommonConst.YES) {
            EntityWrapper<ActivityModel> statusEw = new EntityWrapper<>();
            statusEw.eq("status", CommonConst.YES);
            statusEw.eq("agency_id", model.getAgencyId());
            statusEw.eq("deleted", CommonConst.DELETED_NO);
            Integer statusCount = activityDao.selectCount(statusEw);
            if (statusCount > 0) {
                throw new BusinessException(ActivityRespCode.ACTIVITY_HAS_START);
            }
        }

        activityDao.insert(model);
    }

    @Override
    public Page<ActivityModelVO> activityListByAgencyId(Integer status, Integer agencyId, PageRow pageRow) {
        int count = activityDao.findActivityListByAgencyIdCount(status, agencyId);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<ActivityModelVO> list = activityDao.findActivityListByAgencyIdList(status, agencyId, pageRow);

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public void deleteActivity(Integer id, Integer agencyId) {
        ActivityModel select = new ActivityModel();
        if (agencyId != 0) {
            select.setAgencyId(agencyId);
        }
        select.setId(id);
        select.setDeleted(CommonConst.DELETED_NO);
        ActivityModel model = activityDao.selectOne(select);
        if (model == null) {
            return;
        }
        if (CommonConst.YES == model.getStatus()) {
            throw new BusinessException(ActivityRespCode.ACTIVITY_CAN_NOT_OPT);
        }

        ActivityModel update = new ActivityModel();
        update.setId(id);
        update.setDeleted(CommonConst.DELETED_YES);
        activityDao.updateById(update);

    }

    @Override
    public void updateActivity(ActivityModel model) {
        model.setDeleted(null);

        ActivityModel select = new ActivityModel();
        select.setId(model.getId());
        ActivityModel dbModel = activityDao.selectOne(select);
        if (dbModel == null) {
            return;
        }
        if (model.getStatus() == CommonConst.NO) {
            if (dbModel.getStatus() == CommonConst.YES) {
                throw new BusinessException(ActivityRespCode.ACTIVITY_CAN_NOT_OPT);
            }
        } else {
            EntityWrapper<ActivityModel> codeEw = new EntityWrapper<>();
            codeEw.eq("code", model.getCode());
            codeEw.eq("agency_id", model.getAgencyId());
            codeEw.ne("id", model.getId());
            codeEw.eq("deleted", CommonConst.DELETED_NO);
            Integer count = activityDao.selectCount(codeEw);
            if (count > 0) {
                throw new BusinessException(ActivityRespCode.ACTIVITY_CODE_EXIST);
            }

            EntityWrapper<ActivityModel> statusEw = new EntityWrapper<>();
            statusEw.ne("id", model.getId());
            statusEw.eq("status", CommonConst.YES);
            statusEw.eq("agency_id", model.getAgencyId());
            statusEw.eq("deleted", CommonConst.DELETED_NO);
            Integer statusCount = activityDao.selectCount(statusEw);
            if (statusCount > 0) {
                throw new BusinessException(ActivityRespCode.ACTIVITY_HAS_START);
            }
        }
        model.setUpdateTime(new Date());
        activityDao.updateById(model);
    }

    @Override
    public ActivityModelVO activityDetailByAgencyId(Integer id, Integer agencyId) {
        ActivityModel select = new ActivityModel();
        if (agencyId != 0) {
            select.setAgencyId(agencyId);
        }
        select.setId(id);
        ActivityModel activityModel = activityDao.selectOne(select);

        if (activityModel == null) {
            return null;
        }
        ActivityModelVO vo = new ActivityModelVO();
        BeanUtil.copyNotNullProperties(activityModel, vo);
        if (StringUtil.isNotEmpty(activityModel.getVestlist())) {
            vo.setVestlist(Arrays.asList(StringUtil.split2Int(activityModel.getVestlist())));

        }
        if (StringUtil.isNotEmpty(activityModel.getScopes())) {
            vo.setScopes(Arrays.asList(StringUtil.split2Int(activityModel.getScopes())));

        }
        return vo;
    }

    @Override
    public void updateActivityModelStatus(Integer id, Integer status, Integer agencyId) {
        ActivityModel select = new ActivityModel();
        if (agencyId != 0) {
            select.setAgencyId(agencyId);
        }
        select.setId(id);
        ActivityModel dbModel = activityDao.selectOne(select);
        if (dbModel == null) {
            return;
        }
        if (dbModel.getStatus().equals(status)) {
            return;
        }

        if (status == CommonConst.YES) {
            EntityWrapper<ActivityModel> statusEw = new EntityWrapper<>();
            statusEw.eq("status", CommonConst.YES);
            statusEw.eq("agency_id", agencyId);
            statusEw.eq("deleted", CommonConst.DELETED_NO);
            Integer statusCount = activityDao.selectCount(statusEw);
            if (statusCount > 0) {
                throw new BusinessException(ActivityRespCode.ACTIVITY_HAS_START);
            }
        }
        ActivityModel update = new ActivityModel();
        update.setId(id);
        update.setStatus(status);
        activityDao.updateById(update);
    }

    @Override
    public void pushMsg(Integer id, Integer agencyId) {
        ActivityModel select = new ActivityModel();
        if (agencyId != 0) {
            select.setAgencyId(agencyId);
        }
        select.setId(id);
        ActivityModel model = activityDao.selectOne(select);
        if (model == null) {
            return;
        }

        if (model.getVestlist().isEmpty()) {
            return;
        }
        if (model.getScopes().isEmpty()) {
            return;
        }

        List<Integer> vestlist = Arrays.asList(StringUtil.split2Int(model.getVestlist()));
        List<Integer> scopes = Arrays.asList(StringUtil.split2Int(model.getScopes()));

        List<Integer> pushScopes = new ArrayList<>();
        if (scopes.contains(1)) {
            pushScopes.add(1);
        }
        if (scopes.contains(2)) {
            pushScopes.add(2);
        }
        if (pushScopes.isEmpty()) {
            return;
        }

        PushGroupModel groupModel = new PushGroupModel();
        groupModel.setScopes(scopes);
        groupModel.setVestIds(new HashSet<>(vestlist));
        groupModel.setType(PushEnum.TYPE.NOTICE.value);
        groupModel.setMsg(model.getName());
        if (StringUtil.isNotEmpty(model.getUrl())) {
            groupModel.setIsJump(CommonConst.YES);
            groupModel.setUrl(model.getUrl());
        } else {
            groupModel.setIsJump(CommonConst.NO);
        }
        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_GROUP_MSG, groupModel);

        logger.info("推送活动,data={}", groupModel);

    }
}
