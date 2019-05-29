package org.songbai.loan.service.user.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.encrypt.PasswordEncryptUtil;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.dao.ComUserDao;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Author: qmw
 * Date: 2018/4/18 下午2:31
 */
@Service
public class ComUserServiceImpl implements ComUserService {
    @Autowired
    private ComUserDao comUserDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ComAgencyService comAgencyService;

    @Override
    public UserModel selectUserModelById(Integer userId) {

        return this.selectUserModelById(userId, 1);
    }


    @Override
    public UserModel selectUserModelByThridId(String thridId) {
        return comUserDao.selectUserModelByThridId(thridId);
    }

    @Override
    public UserInfoModel selectUserInfoByThridId(String thridId) {
        UserModel userModel = comUserDao.selectUserModelByThridId(thridId);

        if (userModel != null) {
            return findUserInfoByUserId(userModel.getId());
        }

        return null;
    }

    /**
     * @param userId
     * @param refresh 1 可以去缓存的数据。0 每次取最新的
     * @return
     */
    @Override
    public UserModel selectUserModelById(Integer userId, int refresh) {
        if (refresh == 1) {
            UserModel userModel = (UserModel) redisTemplate.opsForHash().get(UserRedisKey.USER_INFO, userId);
            if (userModel != null) {
                return userModel;
            }
        }
        UserModel model = comUserDao.selectUserModelById(userId);
        if (model != null) {
            redisTemplate.opsForHash().put(UserRedisKey.USER_INFO, userId, model);
        }

        return model;
    }

    @Override
    public Set<Integer> selectUserIdByLikeUserName(String username) {
        return comUserDao.selectUserIdByLikeUserName(username);
    }

    @Override
    public boolean validateLoginPass(Integer userId, String password) {
        UserModel userModel = selectUserModelById(userId);
        if (userModel == null) {
            return false;
        }
        String passEncrypt = PasswordEncryptUtil.digest(password, userModel.getUserPass(), userModel.getPassEncryptTimes());
        return StringUtils.equals(passEncrypt, userModel.getUserPass());
    }

    @Override
    public UserModel findUserModelByAgencyIdAndPhone(String phone, Integer vestId, Integer agencyId) {
        return comUserDao.findUserModelByAgencyIdAndPhone(phone, vestId, agencyId);
    }

    @Override
    public UserInfoModel findUserInfoByUserId(Integer userId, Integer refresh) {
        if (refresh == 1) {
            UserInfoModel model = (UserInfoModel) redisTemplate.opsForHash().get(UserRedisKey.USER_DATA, userId);
            if (model != null) {
                return model;
            }
        }
        UserInfoModel model1 = comUserDao.findUserInfoByUserId(userId);
        if (model1 != null) {
            redisTemplate.opsForHash().put(UserRedisKey.USER_DATA, userId, model1);
        }
        return model1;
    }

    @Override
    public List<UserContactModel> findUserContactListByUserId(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, UserContactModel.class);
    }

    @Override
    public List<UserModel> findUserListByUserPhone(Integer agencyId, String phone) {
        return comUserDao.findUserListByUserPhone(agencyId, phone);
    }

    @Override
    public List<Map<String, String>> findChannelCodeList(Integer agencyId, Integer vestId) {
        List<String> channelCodeList = comUserDao.findChannelCodeList(agencyId, vestId);
        List<Map<String, String>> list = new ArrayList<>();
        for (String channelCode : channelCodeList) {
            Map<String, String> map = new HashMap<>();
            map.put("channelCode", channelCode);
            if (agencyId == null) {
                AgencyChannelModel channel = comUserDao.findChannelNameByChannelCode(channelCode);
                if (channel != null && StringUtil.isNotEmpty(channel.getChannelName())) {
                    map.put("channelName", channel.getChannelName());
                }else {
                    map.put("channelName", channelCode);
                }
            } else {
                AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(agencyId, channelCode);
                if (channel != null && StringUtil.isNotEmpty(channel.getChannelName())) {
                    map.put("channelName", channel.getChannelName());
                }else{
                    map.put("channelName", channelCode);
                }
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public UserInfoModel findUserInfoByUserId(Integer userId) {
        return findUserInfoByUserId(userId, 1);
    }

}
