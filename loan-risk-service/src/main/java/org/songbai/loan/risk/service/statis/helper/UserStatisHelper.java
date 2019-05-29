package org.songbai.loan.risk.service.statis.helper;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.*;
import org.songbai.loan.risk.model.mould.UserRiskStatistic;
import org.songbai.loan.risk.service.statis.dao.*;
import org.songbai.loan.risk.service.statis.util.AgeUtils;
import org.songbai.loan.risk.vo.UserOrderStatVO;
import org.songbai.loan.risk.vo.UserStatisVO;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

/**
 * Author: qmw
 * Date: 2018/11/20 7:43 PM
 */
@Component
public class UserStatisHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserStatisHelper.class);
    @Autowired
    private RiskOrderDao orderDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private RiskUserBlackListDao blackListDao;
    @Autowired
    private RiskBlackListOneDao blackListOneDao;
    @Autowired
    private RiskBlackListTwoDao blackListTwoDao;
    @Autowired
    private RiskUserInfoDao userInfoDao;


    public JSONObject getRiskParam(Integer userid, String orderNumber) {

        UserStatisVO statisVO = userStatis(userid);

        if (statisVO == null) {
            return null;
        }

        JSONObject resultJson = new JSONObject();

        resultJson.put("userreport", JSONObject.toJSON(statisVO));
        resultJson.put("order", JSONObject.toJSON(getUserOrderStat(orderNumber)));
        resultJson.put("userbase", getUserBase(userid));

        return resultJson;
    }


    private UserOrderStatVO getUserOrderStat(String orderNumber) {
        OrderModel query = new OrderModel();

        query.setOrderNumber(orderNumber);

        OrderModel orderModel = orderDao.selectOne(query);

        if (orderModel == null) {
            throw new BusinessException("订单" + orderNumber + "不存在");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(orderModel.getCreateTime());


        UserOrderStatVO vo = new UserOrderStatVO();

        vo.setApplyHour(calendar.get(Calendar.HOUR_OF_DAY));

        return vo;
    }

    private JSONObject getUserBase(Integer userId) {
        UserInfoModel model = comUserService.findUserInfoByUserId(userId);

        JSONObject userbase = new JSONObject();

        userbase.put("age", AgeUtils.getAge(model.getIdcardNum()));
        userbase.put("idcarddeadline", AgeUtils.validateCardno(model.getValidation()));

        return userbase;
    }


    public UserStatisVO userStatis(Integer userId) {

        logger.info("开始查询风控需要的用户信息......userId", userId);

        UserModel userModel = comUserService.selectUserModelById(userId);
        if (userModel == null) {
            UserStatisVO dto = new UserStatisVO();
            dto.setInBlackList(CommonConst.YES);
            return dto;
        }

        UserStatisVO dto = new UserStatisVO();

        queryAndSettingUserBlackProperty(userModel, dto);

        queryAndSettingUserBindProperty(userModel, dto);

        queryAndSettingUserOverdueProperty(userModel, dto);

        queryAndSettingOrderRepeat(userModel, dto);

        saveUserStatisVO(userModel, dto);

        return dto;

    }

    private void queryAndSettingOrderRepeat(UserModel userModel, UserStatisVO dto) {
        // 人工复审拒绝次数
        int customRiskRefuse = orderDao.findOrderCustomRefuseCount(userModel.getId());
        dto.setCustomRiskRefuse(customRiskRefuse);
        // 用户复借次数
        int orderSuccess = orderDao.selectOrderSuccessCount(userModel.getId());
        dto.setRepeatOrderSuccess(orderSuccess);
    }


    private void saveUserStatisVO(UserModel userModel, UserStatisVO dto) {
        String data = JSONObject.toJSONString(dto);
        if (logger.isInfoEnabled()) {
            logger.info("用户风控统计数据,userId={},data={}", userModel.getId(), data);
        }
        Query queryMongo = new Query();
        queryMongo.addCriteria(Criteria.where("userId").is(userModel.getId()));
        queryMongo.addCriteria(Criteria.where("phone").is(userModel.getPhone()));
        Update update = new Update();
        update.set("phone", userModel.getPhone());
        update.set("userId", userModel.getId());
        update.set("data", data);
        mongoTemplate.upsert(queryMongo, update, UserRiskStatistic.class);
    }

    /**
     * 用户逾期属性
     */
    public void queryAndSettingUserOverdueProperty(UserModel userModel, UserStatisVO dto) {

        LocalDate today = LocalDate.now();

        //最后一笔的预期天数
        OrderModel orderModel = orderDao.findRecentOrderByUserId(userModel.getId());
        if (orderModel != null) {
            if (orderModel.getRepaymentDate() != null) {
                LocalDate repayDate = Date8Util.date2LocalDate(orderModel.getRepaymentDate());

                Period mp = Period.between(today, repayDate);
                if (mp.getDays() > 0) {
                    dto.setLastOrderExceedDays(mp.getDays());
                }
            }
        }
        //累计预期次数
        int exceedCount = orderDao.findOrderOverdueCountByUserId(userModel.getId());
        dto.setExceedCount(exceedCount);

        //累计逾期3天及以上次数
        int exceedThan3Days = orderDao.findOrderOverdueCountByUserIdAndDays(userModel.getId(), 3);
        dto.setExceedThan3Days(exceedThan3Days);

        //累计逾期10天及以上次数
        int exceedThan10Days = orderDao.findOrderOverdueCountByUserIdAndDays(userModel.getId(), 10);
        dto.setExceedThan10Days(exceedThan10Days);
    }

    /**
     * 用户绑定属性
     */
    public void queryAndSettingUserBindProperty(UserModel userModel, UserStatisVO dto) {
        //设备绑定过账号数量
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userModel.getId()));
        int accountDeviceBindCount = (int) mongoTemplate.count(query, UserDeviceModel.class);
        dto.setAccountDeviceBindCount(accountDeviceBindCount);

        if (StringUtil.isNotEmpty(userModel.getDeviceId())) {
            //账号绑定过的设备数量
            query = new Query();
            query.addCriteria(Criteria.where("device").is(userModel.getDeviceId()));
            int deviceAccountBindCount = (int) mongoTemplate.count(query, UserDeviceModel.class);
            dto.setDeviceAccountBindCount(deviceAccountBindCount);
        }
    }

    /**
     * 用户黑名单属性
     */
    public void queryAndSettingUserBlackProperty(UserModel userModel, UserStatisVO dto) {
        switch (userModel.getStatus()) {
            case 0: //黑
                dto.setInBlackList(CommonConst.YES);
                break;
            case 2: //灰
                dto.setInGrayList(CommonConst.YES);
                break;
            case 3: //白
                dto.setInWhiteList(CommonConst.YES);
                break;
            default: //正常
                UserBlackListModel queryBlackList = new UserBlackListModel();
                queryBlackList.setUserId(userModel.getId());

                UserBlackListModel blackListModel = blackListDao.selectOne(queryBlackList);
                if (blackListModel != null) {
                    switch (blackListModel.getType()) {
                        case 0:
                            dto.setInBlackList(CommonConst.YES);
                            break;
                        case 2:
                            dto.setInGrayList(CommonConst.YES);
                            break;
                        case 3:
                            dto.setInWhiteList(CommonConst.YES);
                            break;
                    }
                } else {
                    BlackListTwoModel queryBlackListTwo = new BlackListTwoModel();
                    queryBlackListTwo.setPhone(userModel.getPhone());

                    BlackListTwoModel blackListTwo = blackListTwoDao.selectOne(queryBlackListTwo);
                    if (blackListTwo != null) {
                        dto.setInBlackList(CommonConst.YES);
                    } else {
                        UserInfoModel queryUserInfoModel = new UserInfoModel();
                        queryUserInfoModel.setUserId(userModel.getId());

                        UserInfoModel userInfoModel = userInfoDao.selectOne(queryUserInfoModel);
                        if (userInfoModel != null) {
                            BlackListOneModel queryBlackListOne = new BlackListOneModel();
                            queryBlackListOne.setIdcardNum(userInfoModel.getIdcardNum());

                            BlackListOneModel blackListOne = blackListOneDao.selectOne(queryBlackListOne);
                            if (blackListOne != null) {
                                dto.setInBlackList(CommonConst.YES);
                            }
                        }

                    }
                }
        }
    }
}
