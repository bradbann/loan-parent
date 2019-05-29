package org.songbai.loan.push.jms;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.concurrent.Executors;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.sms.PushGroupModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.sms.PushSenderModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.push.dao.AdminVestDao;
import org.songbai.loan.push.dao.PushSenderDao;
import org.songbai.loan.push.helper.PushGroupHelper;
import org.songbai.loan.push.model.GexingDTO;
import org.songbai.loan.push.service.PushService;
import org.songbai.loan.push.util.PushUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.songbai.loan.constant.sms.PushEnum.Classify.GROUP;

/**
 * Author: qmw
 * Date: 2018/11/22 2:06 PM
 */
@Component
public class PushMqListener {

    public static Logger logger = LoggerFactory.getLogger(PushMqListener.class);

    private Map<Integer, Object> handMap = Maps.newConcurrentMap();//代理个推本地缓存
    private final static long EXPIRED_TIME = 5 * 60 * 1000L;//过期时间5分钟

    @Autowired
    PushService pushService;
    @Autowired
    private PushSenderDao pushSenderDao;
    @Autowired
    private AdminVestDao vestDao;
    @Autowired
    private PushGroupHelper groupHelper;

    private ExecutorService executorService = Executors.newFixedThreadPool(1, 2, "push-group");

    @JmsListener(destination = JmsDest.LOAN_PUSH_MSG)
    public void sendPush(PushModel pushModel) {
        if (pushModel == null) return;
        try {
            logger.info("PushMqListener,pushModel={}", pushModel);

            if (pushModel.getVestId() == null) {
                logger.info("推送消息马甲id不能为空", pushModel);
                return;
            }

            GexingDTO gexingInfo = getAgencyGexingInfo(pushModel.getVestId());
            if (CommonConst.NO == gexingInfo.getExist()) {
                logger.info("vestId={},没有配置或未启用个推账号", pushModel.getVestId());
                return;
            }
            pushService.push(pushModel, gexingInfo);

        } catch (Exception e) {
            logger.info("push error {}", e);
        }
    }


    @JmsListener(destination = JmsDest.LOAN_PUSH_GROUP_MSG)
    public void groupPush(PushGroupModel groupModel) {
        if (groupModel == null) return;
        logger.info("PushMqListener,pushModel={}", groupModel);


        if (groupModel.getVestIds().isEmpty()) {
            logger.info("推送消息马甲id不能为空", groupModel);
            return;
        }
        if (groupModel.getScopes().isEmpty()) {
            logger.info("没有推送的客户端类型,data={}", groupModel);
            return;
        }

        List<String> scopces = new ArrayList<>();
        for (Integer scope : groupModel.getScopes()) {
            if (1 == scope) {
                scopces.add("android");
            } else if (2 == scope) {
                scopces.add("ios");
            }
        }
        if (scopces.isEmpty()) {
            logger.info("解析出没有推送的客户端类型,data={}", groupModel);
            return;
        }

        for (Integer vestId : groupModel.getVestIds()) {
            executorService.submit(() -> {
                try {
                    GexingDTO gexingInfo = getAgencyGexingInfo(vestId);
                    if (CommonConst.NO == gexingInfo.getExist()) {
                        logger.info("vestId={},没有配置或未启用个推账号", vestId);
                        return;
                    }

                    Set<String> deviceIds = groupHelper.getPushDevices(groupModel, vestId,scopces);
                    if (CollectionUtils.isEmpty(deviceIds)) {
                        logger.info("要推送的集合不存在或为空");
                    }
                    PushModel model = new PushModel();
                    model.setClassify(GROUP.value);
                    model.setType(groupModel.getType());
                    model.setTitle(groupModel.getTitle());
                    model.setMsg(groupModel.getMsg());
                    model.setUrl(groupModel.getUrl());
                    model.setIsJump(groupModel.getIsJump());

                    model.setDeviceIds(deviceIds);

                    logger.info("vestId={},按组推送内容={},设备集合={}", vestId, model.getMsg(), deviceIds);

                    PushUtils.sendToList(model, gexingInfo.getAppId(), gexingInfo.getAppKey(), gexingInfo.getMaster(), gexingInfo.getUrl());
                } catch (Exception e) {
                    logger.info("push loop group error {}", e);
                }
            });
        }
    }

    /**
     * 获取马甲个推信息,缓存时间5分钟
     */
    private GexingDTO getAgencyGexingInfo(Integer vestId) {
        Long now = System.currentTimeMillis();
        GexingDTO dto = (GexingDTO) handMap.get(vestId);
        if (isInvalid(now, handMap, dto, vestId)) {
            return dto;
        }

        GexingDTO gexingDTO = new GexingDTO();
        gexingDTO.setExpired(now + EXPIRED_TIME);

        AppVestModel vest = vestDao.findVestById(vestId);
        if (vest == null || vest.getPushSenderId() == null) {
            gexingDTO.setExist(CommonConst.NO);
            handMap.put(vestId, gexingDTO);
            return gexingDTO;
        }

        PushSenderModel sender = pushSenderDao.selectById(vest.getPushSenderId());
        if (sender == null || sender.getStatus() == CommonConst.STATUS_INVALID) {
            gexingDTO.setExist(CommonConst.NO);
        } else {
            gexingDTO.setExist(CommonConst.YES);
            BeanUtils.copyProperties(sender, gexingDTO);
        }

        handMap.put(vestId, gexingDTO);
        return gexingDTO;
    }

    private boolean isInvalid(Long now, Map<Integer, Object> map, GexingDTO cacheDto, Integer key) {
        if (cacheDto == null) return false;
        if (now > cacheDto.getExpired()) {
            map.remove(key);
            return false;
        }
        return true;
    }
}
