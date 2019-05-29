package org.songbai.loan.push.helper;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.sms.PushGroupModel;
import org.songbai.loan.push.dao.OrderDao;
import org.songbai.loan.push.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Author: qmw
 * Date: 2019/1/14 5:27 PM
 */
@Component
public class PushGroupHelper {

    private static final Logger logger = LoggerFactory.getLogger(PushGroupHelper.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;

    public Set<String> getPushDevices(PushGroupModel groupModel, Integer vestId, List<String> scopces) {
        PushEnum.TYPE pushType = PushEnum.TYPE.parse(groupModel.getType());

        if (pushType == null) {
            logger.info("推送消息类别不存在,type={}", groupModel.getType());
            return null;
        }
        switch (pushType) {
            case NOTICE:
                return userDao.findDevicesByVestId(vestId, scopces);
            case ORDER:
                return getOrderSubPushDevice(groupModel, vestId, scopces);
            default:
                logger.info("程序暂未有推送类型type={}的处理", pushType.code);
                return null;
        }
    }


    private Set<String> getOrderSubPushDevice(PushGroupModel groupModel, Integer vestId, List<String> scopces) {
        PushEnum.LOAN subType = PushEnum.LOAN.parse(groupModel.getSubType());
        if (subType == null) {
            logger.info("推送消息子类别不存在,type={}", groupModel.getSubType());
            return null;
        }
        switch (subType) {
            case REPAY_REMIND:
                return orderDao.findTodayRepayOrder(LocalDate.now(), vestId, scopces);
            case LOAN_OVERDUE:

                JSONObject jsonObject = groupModel.getCondition();
                if (jsonObject == null) {
                    logger.info("用户逾期提醒推送,条件集为空");
                    return null;
                }

                Integer exceedDays = jsonObject.getInteger("day");
                Double exceedFee = jsonObject.getDouble("money");
                if (exceedFee == null || exceedDays == null) {
                    logger.info("用户逾期提醒推送,条件集为空,参数={}", jsonObject.toJSONString());
                    return null;
                }

                return orderDao.findOrderOverdueUserDeviceId(exceedDays, exceedFee, vestId, scopces);

            default:
                logger.info("程序暂未有推送子类型subType={}的处理", subType.code);
                return null;
        }

    }
}
