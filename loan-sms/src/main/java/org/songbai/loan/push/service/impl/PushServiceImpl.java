package org.songbai.loan.push.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.sms.PushSenderModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.push.service.PushService;
import org.songbai.loan.push.util.PushUtils;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: qmw
 * Date: 2018/11/14 11:26 AM
 */
@Service
public class PushServiceImpl implements PushService {
    private static final Logger LOG = LoggerFactory.getLogger(PushServiceImpl.class);

    @Autowired
    private ComUserService comUserService;

    public void push(PushModel model, PushSenderModel sender) {
        PushEnum.Classify classify = PushEnum.Classify.parse(model.getClassify());

        if (classify == null) {
            LOG.info("推送消息类别不存在", sender.getAppId(), sender.getAppKey(), sender.getMaster());
            return;
        }
        try {
            switch (classify) {
                case SYSTEM:
                    PushUtils.sendAll(model, sender.getAppId(), sender.getAppKey(), sender.getMaster(), sender.getUrl());
                    break;
                case GROUP:
                    if (model.getDeviceIds() != null && model.getDeviceIds().size() > 0) {
                        PushUtils.sendToList(model, sender.getAppId(), sender.getAppKey(), sender.getMaster(), sender.getUrl());
                    }
                    break;
                case SINGLE:
                    UserModel u = comUserService.selectUserModelById(model.getUserId());
                    if (u == null || StringUtils.isEmpty(u.getGexing())) {
                        LOG.info("推送>>>拒绝,用户id={},没有个推id,无法推送", model.getUserId());
                        break;
                    }
                    model.setDeviceId(u.getGexing());

                    String str = PushUtils.sendSingle(model, sender.getAppId(), sender.getAppKey(), sender.getMaster(), sender.getUrl());
                    LOG.info("单推>>>推送的结果{}", str);
                    break;
            }

            LOG.info("结束处理推送消息的MQ,pushModel={}", model);
        } catch (Exception e) {
            LOG.warn("推送信息出错", e);
        }
    }
}
