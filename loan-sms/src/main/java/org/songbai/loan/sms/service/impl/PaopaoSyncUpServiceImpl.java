package org.songbai.loan.sms.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.push.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("paopaoSyncUp")
public class PaopaoSyncUpServiceImpl extends SmsSyncUpAbstractServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(PaopaoSyncUpServiceImpl.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private SpringProperties springProperties;
    @Override
    protected void syncMsg(SmsSender sender) {

        Map<String, String> data = new HashMap<>();
        data.put("action", "mo");
        data.put("account", sender.getAccount());
        data.put("password", sender.getPassword());
        data.put("rt", "json");
        String url = springProperties.getString("sms.paopaoyun.url", "http://ppyw.paopao106.cn:7862/sms");

        String result = HttpTools.doPost(url, data);

        System.out.println(result);
        if (StringUtil.isEmpty(result)) {
            logger.info("泡泡云上行短信同步>>>,查询结果过为空,sender={}", sender);
            return;
        }

        JSONObject ret = JSONObject.parseObject(result);
        if (!ret.getString("status").equals("0")) {
            logger.info("泡泡云上行短信同步>>>,查询结果错误,sender={},result={}", sender, result);
            return;
        }
        JSONArray list = ret.getJSONArray("list");

        for (int i = 0; i < list.size(); i++) {

            JSONObject jsonObject = list.getJSONObject(i);

            UserFeedbackModel feedbackModel = new UserFeedbackModel();

            feedbackModel.setFeedbackTime(jsonObject.getDate("time"));
            String phone = jsonObject.getString("mobile");
            feedbackModel.setPhone(phone);
            feedbackModel.setAgencyId(sender.getAgencyId());
            feedbackModel.setContent("【泡泡云短信回复】" + jsonObject.getString("content"));


            List<UserModel> userList = userDao.findUserByPhoneAndAgencyId(phone, sender.getAgencyId());
            if (userList.isEmpty()) {
                logger.info("泡泡云上行短信同步>>>,未查到手机号={},agencyId={},用户信息", phone, sender.getAgencyId());
                continue;
            }

            if (userList.size() == 1) {
                UserModel  user = userList.get(0);
                feedbackModel.setVestId(user.getVestId());
                feedbackModel.setUserId(user.getId());
                feedbackModel.setName(user.getName());
            } else {
                UserModel user = super.findUserBySmsLog(sender.getAgencyId(), sender.getType());
                if (user == null) {
                    logger.info("泡泡云上行短信同步>>>,短信日志未找到,不插入用户名称,马甲和id");
                }else {
                    feedbackModel.setVestId(user.getVestId());
                    feedbackModel.setName(user.getName());
                    feedbackModel.setUserId(user.getId());
                }
            }

            getInsertDao().insert(feedbackModel);
            logger.info("泡泡云上行短信同步>>>,插入用户上行短信,data={}", feedbackModel);

        }
    }
}
