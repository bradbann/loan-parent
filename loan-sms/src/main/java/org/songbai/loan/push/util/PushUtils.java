package org.songbai.loan.push.util;

import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.model.sms.PushModel;

import java.util.*;

public class PushUtils {

    private static Logger logger = LoggerFactory.getLogger(PushUtils.class);

    /**
     * 推送单个用户
     */
    public static String sendSingle(PushModel pushModel, String appId, String appKey, String masterSecret, String url) {

        String cid = pushModel.getDeviceId();

        IGtPush push = new IGtPush(url, appKey, masterSecret);
        try {
            //记录日志
            logger.info("推送开始,设备={},内容={}", cid, pushModel.getMsg());

            TransmissionTemplate template = getTemplate(pushModel, appId, appKey);
            //接收者
            Target target = new Target();
            //接收者安装的应用的APPID
            target.setAppId(appId);
            //接收者的ClientID
            target.setClientId(cid);
            //单推消息类型
            SingleMessage message = new SingleMessage();
            //用户当前不在线时，是否离线存储,可选
            message.setOffline(true);
            //离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(1000 * 60);
            message.setData(template);
            try {
                //单推
                IPushResult ret = push.pushMessageToSingle(message, target);
                logger.info("推送结束,设备={},内容={}", cid, pushModel.getMsg());
                return ret.getResponse().toString();
            } catch (RequestException e) {
                String requstId = e.getRequestId();
                IPushResult ret = push.pushMessageToSingle(message, target, requstId);
                logger.info("推送结束,设备={},内容={}", cid, pushModel.getMsg());
                return ret.getResponse().toString();
            }
        } catch (Exception ex) {
            logger.warn("发送出错 设备={}", cid, ex);
            return "";
        }
    }


    /**
     * 单传模板
     */
    private static TransmissionTemplate getTemplate(PushModel model, String appId, String appKey) throws Exception {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);

        //这个是给android 用的
        String title = model.getTitle();
        String msg = model.getMsg();
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("msg", msg);
        map.put("dataId", model.getDataId());
        map.put("type", model.getType());
        map.put("classify", model.getClassify());
        map.put("createTime", System.currentTimeMillis());
        map.put("url", model.getUrl());
        map.put("isJump", model.getIsJump());
        template.setTransmissionType(2);
        template.setTransmissionContent(JSON.toJSONString(map));

        //这个是给ios 用的
        APNPayload payload = new APNPayload();
        payload.setContentAvailable(1);
        payload.setSound("default");
        payload.addCustomMsg("title", title);
        map.put("isJump", model.getIsJump());
        payload.addCustomMsg("msg", msg);
        payload.addCustomMsg("dataId", model.getDataId());
        payload.addCustomMsg("type", model.getType());
        payload.addCustomMsg("classify", model.getClassify());
        payload.addCustomMsg("createTime", System.currentTimeMillis());
        payload.addCustomMsg("url", model.getUrl());
        // payload.setCategory("$由客户端定义")
        // 字典模式使用下者
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();


        alertMsg.setBody(msg);
        alertMsg.setTitle(title);
        payload.setAlertMsg(alertMsg);
        template.setAPNInfo(payload);
        return template;
    }

    public static NotificationTemplate getTemplateForList(PushModel model, String appId, String appKey) throws Exception {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(model.getTitle());
        style.setText(model.getMsg());
        // 配置通知栏图标
        // style.setLogo("icon.png");
        // 配置通知栏网络图标
        //style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent(model.getMsg());
        return template;
    }

    public static void sendAll(PushModel pushModel, String appId, String appkey, String master, String url) throws Exception {
        IGtPush push = new IGtPush(url, appkey, master);

        AppMessage message = new AppMessage();
        message.setData(getTemplate(pushModel, appId, appkey));
        message.setAppIdList(Lists.newArrayList(appId));
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);

        IPushResult ret = push.pushMessageToApp(message);

        logger.info("推送全部设备的结果 {}", ret.getResponse());
    }

    public static void sendToList(PushModel pushModel, String appId, String appKey, String masterSecret, String url) {
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        try {
            Set<String> list = pushModel.getDeviceIds();
            if (list.size() <= 0) {
                logger.info("没有要推送的用户群");
                return;
            }
            Iterator iterator = list.iterator();
            List<String> newList = new ArrayList<>();
            Integer count = 0;
            boolean flag;
            do {
                //每次推送的用户数量
                Integer MAX_TARGET = 100;
                if (count != 0 && count % MAX_TARGET == 0 && newList.size() > 0) {
                    flag = true;
                } else {
                    newList.add((String) iterator.next());
                    flag = list.size() - count < MAX_TARGET && count == list.size() - 1;

                    count++;
                }
                if (flag) {
                    logger.info("skip:{}, limit:{}", count, MAX_TARGET);
                    ListMessage message = new ListMessage();
                    message.setData(getTemplate(pushModel, appId, appKey));
                    message.setOffline(true);
                    message.setOfflineExpireTime(1000 * 600);
                    List<Target> targets = new ArrayList<>();
                    if (pushModel.getDeviceIds() != null && pushModel.getDeviceIds().size() > 0) {
                        for (String deviceId : newList) {
                            Target target = new Target();
                            if (StringUtil.isNotEmpty(deviceId)) {
                                target.setAppId(appId);
                                target.setClientId(deviceId);
                                targets.add(target);
                            }
                        }
                    }
                    if (targets.size() <= 0) {
                        logger.info("没有要推送的用户群");
                        return;
                    }
                    String taskId = push.getContentId(message);
                    IPushResult ret = push.pushMessageToList(taskId, targets);
                    logger.info("推送指定用户群的结果 {}", ret.getResponse());
                    newList.clear();
                }
            }
            while (iterator.hasNext());
        } catch (Exception ex) {
            logger.warn("推送指定用户群出错 {} ", pushModel.getDeviceIds(), ex);
        }
    }
}