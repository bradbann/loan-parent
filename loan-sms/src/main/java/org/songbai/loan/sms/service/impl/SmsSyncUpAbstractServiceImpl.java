package org.songbai.loan.sms.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.push.dao.UserDao;
import org.songbai.loan.sms.dao.UserFeedbackDao;
import org.songbai.loan.sms.model.SmsLog;
import org.songbai.loan.sms.service.SmsSyncUpAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: qmw
 * Date: 2019/1/22 1:52 PM
 */
public abstract class SmsSyncUpAbstractServiceImpl implements SmsSyncUpAbstractService {

    private static final Logger logger = LoggerFactory.getLogger(SmsSyncUpAbstractServiceImpl.class);

    @Autowired
    UserFeedbackDao userFeedbackDao;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private UserDao userDao;

    @Override
    public void msgSyncUp(SmsSender sender) {
        syncMsg(sender);

    }

    protected abstract void syncMsg(SmsSender sender);


    UserFeedbackDao getInsertDao() {
        return userFeedbackDao;
    }

    /**
     * 写这里是因为以后所有的根据用户查询上行日志的条件应该是一致的
     *
     * @return
     */
    UserModel findUserBySmsLog(Integer agencyId, Integer senderType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("agencyId").is(agencyId));
        query.addCriteria(Criteria.where("senderType").is(senderType));

        Date date = Date8Util.LocalDate2Date(LocalDate.now().minusDays(2));
        query.addCriteria(Criteria.where("createTime").gte(date));

        List<SmsLog> smsLogs = mongoTemplate.find(query, SmsLog.class);
        if (CollectionUtils.isEmpty(smsLogs)) {
            return null;
        }
        boolean same = false;
        SmsLog smsLog = null;
        if (smsLogs.size() == 1) {
            same = true;
            smsLog = smsLogs.get(0);

        } else {
            Map<Integer, List<SmsLog>> collect = smsLogs.stream().collect(Collectors.groupingBy(SmsLog::getVestId));
            Set<Integer> keys = collect.keySet();
            if (keys.size() <= 1) {
                same = true;
                smsLog = smsLogs.get(0);
            }
        }
        if (same) {
            return userDao.findUserBySmsLos(smsLog);
        }
        return null;


    }
}
