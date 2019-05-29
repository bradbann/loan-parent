package org.songbai.loan.admin.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.user.service.CallReportService;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.risk.moxie.carrier.model.*;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallReportServiceImpl implements CallReportService {

    @Autowired
    private ComUserService comUserService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> callReport(String userId, Integer agencyId, int page, int pageSize) {

        //先查询用户信息
        UserModel user = comUserService.selectUserModelByThridId(userId);
        if (user == null) return null;

        if (agencyId != 0 && !agencyId.equals(user.getAgencyId())) return null;


        Map<String, String> contactMap = queryContacts(user);
        Map<String, Object> result = new HashMap<>();


        result.put("mobileBasic", queryMobileBasic(user));
        result.put("familyMember", queryFamilyMember(user, contactMap));
        result.put("packageUsage", queryFlowUsed(user));
        result.put("smsSend", queryMobileSms(user, "SEND", contactMap, new PageRequest(page, pageSize)));
        result.put("smsReceive", queryMobileSms(user, "RECEIVE", contactMap, new PageRequest(page, pageSize)));
        result.put("recharge", queryMobileRecharge(user));
        result.put("voiceCallDial", queryMobileVoiceCall(user, "DIAL", contactMap, new PageRequest(page, pageSize)));
        result.put("voiceCallDialed", queryMobileVoiceCall(user, "DIALED", contactMap, new PageRequest(page, pageSize)));


        return result;
    }

    @Override
    public List<Object> queryMobileSms(String userId, Integer agencyId, String sendType, int page, int pageSize) {

        //先查询用户信息
        UserModel user = comUserService.selectUserModelByThridId(userId);
        if (user == null) return null;

        if (agencyId != 0 && !agencyId.equals(user.getAgencyId())) return null;
        Map<String, String> contactMap = queryContacts(user);

        return queryMobileSms(user, sendType, contactMap, new PageRequest(page, pageSize));
    }

    @Override
    public List<Object> queryVoiceCall(String userId, String dialType, Integer agencyId, int page, int pageSize) {

        //先查询用户信息
        UserModel user = comUserService.selectUserModelByThridId(userId);
        if (user == null) return null;

        if (agencyId != 0 && !agencyId.equals(user.getAgencyId())) return null;
        Map<String, String> contactMap = queryContacts(user);

        return queryMobileVoiceCall(user, dialType, contactMap, new PageRequest(page, pageSize));
    }

    private Map<String, String> queryContacts(UserModel user) {

        Query query = Query.query(Criteria.where("userId").is(user.getId()));

        List<UserContactModel> lists = mongoTemplate.find(query, UserContactModel.class);


        HashMap<String, String> resultMap = new HashMap<>();
        for (UserContactModel model : lists) {

            resultMap.put(model.getPhone(), model.getName());
        }

        return resultMap;
    }

    private List<MobileBasicModel> queryMobileBasic(UserModel user) {
        Query query = Query.query(Criteria.where("userId").is(user.getThirdId()).and("mobile").is(user.getPhone()));

        return mongoTemplate.find(query, MobileBasicModel.class);
    }

    private List<Object> queryFamilyMember(UserModel user, Map<String, String> contactMap) {
        Query query = Query.query(Criteria.where("userId").is(user.getThirdId()).and("mobile").is(user.getPhone()));
        query.with(new Sort(Sort.Direction.ASC, "familyNetNum", "shortNumber"));

        List<FamilyMemberModel> memberModelList = mongoTemplate.find(query, FamilyMemberModel.class);

        List<Object> resultList = new ArrayList<>();

        for (FamilyMemberModel model : memberModelList) {

            JSONObject temp = (JSONObject) JSONObject.toJSON(model);

            String name = contactMap.getOrDefault(model.getLongNumber(), "");

            if (StringUtil.isEmpty(name)) {
                if ("MASTER".equalsIgnoreCase(model.getMemberType())) {
                    name = "家长";
                } else {
                    name = "成员";
                }
                if (user.getPhone().equalsIgnoreCase(model.getLongNumber())) {
                    name = "本人";
                }
            }

            temp.put("memberName", name);

            resultList.add(temp);
        }

        return resultList;
    }


    private List<PackageUsageModel> queryFlowUsed(UserModel user) {

        Query query = Query.query(Criteria.where("userId").is(user.getThirdId()).and("mobile").is(user.getPhone()));
        query.with(new Sort(Sort.Direction.DESC, "billEndDate"));

        return mongoTemplate.find(query, PackageUsageModel.class);
    }


    private List<Object> queryMobileSms(UserModel user, String sendType, Map<String, String> contactMap, Pageable pageable) {

        Criteria criteria = Criteria.where("userId").is(user.getThirdId())
                .and("mobile").is(user.getPhone())
                .and("peerNumber").nin("10086", "10001", "10010", "10000", "10011")
                .and("serviceName").nin("梦网业务");


        if (StringUtil.isNotEmpty(sendType)) {
            criteria.and("sendType").is(sendType);
        }

        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        query.with(pageable);


        List<MobileSmsModel> modelList = mongoTemplate.find(query, MobileSmsModel.class);

        List<Object> resultList = new ArrayList<>();

        for (MobileSmsModel model : modelList) {

            JSONObject temp = (JSONObject) JSONObject.toJSON(model);
            temp.put("peerName", contactMap.get(model.getPeerNumber()));
            temp.put("label", "");

            resultList.add(temp);
        }

        return resultList;
    }

    private List<MobileRechargeModel> queryMobileRecharge(UserModel user) {

        Criteria criteria = Criteria.where("userId").is(user.getThirdId())
                .and("mobile").is(user.getPhone());

        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "rechargeTime"));

        return mongoTemplate.find(query, MobileRechargeModel.class);
    }

    private List<Object> queryMobileVoiceCall(UserModel user, String dialType, Map<String, String> contactMap, Pageable pageable) {

        Criteria criteria = Criteria.where("userId").is(user.getThirdId())
                .and("mobile").is(user.getPhone())
                .and("peerNumber").nin("10086", "10001", "10010", "10000", "10011");

        if (StringUtil.isNotEmpty(dialType)) {
            criteria.and("dialType").is(dialType);
        }

        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        query.with(pageable);

        List<MobileVoiceCallModel> modelList = mongoTemplate.find(query, MobileVoiceCallModel.class);

        List<Object> resultList = new ArrayList<>();

        for (MobileVoiceCallModel model : modelList) {

            JSONObject temp = (JSONObject) JSONObject.toJSON(model);
            temp.put("peerName", contactMap.get(model.getPeerNumber()));
            temp.put("label", "");

            resultList.add(temp);
        }

        return resultList;
    }
}
