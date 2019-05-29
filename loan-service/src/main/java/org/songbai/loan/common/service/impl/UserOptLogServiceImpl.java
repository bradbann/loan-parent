package org.songbai.loan.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.loan.common.service.UserOptLogService;
import org.songbai.loan.common.util.AgentKitImpl;
import org.songbai.loan.constant.PlatformEnum;
import org.songbai.loan.constant.user.UserConstant;
import org.songbai.loan.model.user.UserOptLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserOptLogServiceImpl implements UserOptLogService {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, String beforeValue, String afterValue) {

        UserOptLogModel model = createUserOptRecordModel(userid, ip, browserAgent, optType, beforeValue, afterValue);

        mongoTemplate.save(model);
    }


    @Override
    public void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType) {

        save(userid, ip, browserAgent, optType, null);
    }

    @Override
    public void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, PlatformEnum device) {
        UserOptLogModel model = createUserOptRecordModel(userid, ip, browserAgent, optType, device, null, null);

        mongoTemplate.save(model);
    }


    @Override
    public List<UserOptLogModel> queryList(Integer userId, UserConstant.Opt optType, Date endTime, int size) {
        Query query = new Query();

        query.addCriteria(Criteria.where("userId").is(userId).and("optType").is(optType.key));

        if (endTime != null) {
            query.addCriteria(Criteria.where("createTime").lt(endTime));
        }

        query.limit(size);
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        return mongoTemplate.find(query, UserOptLogModel.class);
    }


    private UserOptLogModel createUserOptRecordModel(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, String beforeValue, String afterValue) {
        return createUserOptRecordModel(userid, ip, browserAgent, optType, null, beforeValue, afterValue);
    }

    private UserOptLogModel createUserOptRecordModel(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, PlatformEnum device, String beforeValue, String afterValue) {
        UserOptLogModel model = new UserOptLogModel();

        model.setUserId(userid);
        model.setIp(ip);
        model.setBrowserAgent(browserAgent);
        model.setOptType(optType.key);
        model.setBeforeValue(beforeValue);
        model.setAfterValue(afterValue);

        model.setIpAddr(checkRegion(ip));
        model.setCreateTime(new Date());
        if (device != null) {
            model.setDevice(device.name());
        } else {
            if(StringUtils.isNotEmpty(browserAgent)){
                if (AgentKitImpl.isAndroid(browserAgent)) {
                    model.setDevice(PlatformEnum.Android.name());
                } else if (AgentKitImpl.isIphone(browserAgent)) {
                    model.setDevice(PlatformEnum.IOS.name());
                } else if (AgentKitImpl.isApple(browserAgent)) {
                    model.setDevice(PlatformEnum.H5.name());
                } else {
                    model.setDevice(PlatformEnum.Web.name());
                }
            }
        }
        return model;
    }

    private String checkRegion(String ip) {

        if (StringUtils.isEmpty(ip)) {
            return ip;
        }
        try {
            HashMap<String, String> param = new HashMap<>();
            param.put("ip", ip);
            String result = HttpTools.doGet("http://lemi.esongbai.com/prevent/ip/find.do", param);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger("code") == 200) {

                JSONObject data = jsonObject.getJSONObject("data");

                return shortTrimToEmpty(data.getString("country")) +
                        " " +
                        shortTrimToEmpty(data.getString("city"));
            }
        } catch (Exception e) {
            //Ignore
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("xx"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("XX"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("city"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("中"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("中xx"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("se"));
        System.out.println(new UserOptLogServiceImpl().shortTrimToEmpty("folorada"));
    }

    private String shortTrimToEmpty(String data) {

        data = StringUtils.trimToEmpty(data);

        return data.getBytes().length < 4 ? "" : data;
    }


}
