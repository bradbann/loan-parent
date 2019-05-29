package org.songbai.loan.risk.service.statis.helper;

import com.alibaba.fastjson.JSONObject;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.risk.vo.UserContactVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserContactHelper {

    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * userid 用户的真实ID
     *
     * @param userid
     * @return
     */
    public JSONObject getRiskParam(Integer userid) {

        UserContactVO contactVO = getUserContactVO(userid);

        if (contactVO == null) {
            return null;
        }

        JSONObject resultJson = new JSONObject();

        resultJson.put("contact", JSONObject.toJSON(contactVO));

        return resultJson;
    }

    private UserContactVO getUserContactVO(Integer userid) {
        Query query = Query.query(Criteria.where("userId").is(userid));

        List<UserContactModel> contactModelList = mongoTemplate.find(query, UserContactModel.class);


        UserContactVO contactVO = new UserContactVO();

        contactVO.setUserCount(contactModelList.size());
        return contactVO;
    }


    public List<String> getContactPhoneList(Integer userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));

        List<UserContactModel> contactModelList = mongoTemplate.find(query, UserContactModel.class);


        return contactModelList.parallelStream().map(UserContactModel::getPhone).collect(Collectors.toList());
    }


}
