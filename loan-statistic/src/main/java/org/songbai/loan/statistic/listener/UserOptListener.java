package org.songbai.loan.statistic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.statistic.dao.UserActionStatisticDao;
import org.songbai.loan.statistic.dao.UserStatisticDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Author: qmw
 * Date: 2018/11/27 8:03 PM
 * 用户行为操作统计
 */
@Component
public class UserOptListener {
    private static final Logger logger = LoggerFactory.getLogger(UserOptListener.class);
    @Autowired
    private UserStatisticDao userStatisticDao;
    @Autowired
    private UserActionStatisticDao userActionStatisticDao;

    /**
     * 用户统计
     */
    @JmsListener(destination = JmsDest.USER_STATISTIC)
    public void userStatistic(UserStatisticDTO dto) {
        logger.info("用户统计>>>>,data={}", dto);
        if (dto == null) {
            logger.info("用户统计>>>>,数据为空", dto);
            return;
        }
        if (dto.getAgencyId() == null || dto.getRegisterDate() == null || dto.getActionDate() == null || dto.getVestId() == null || StringUtil.isEmpty(dto.getChannelCode())|| dto.getIsActionLogin() == null) {
            logger.info("用户统计>>>>,agencyId和registerDate,actionDate,vestId,channelCode不能为空,dto={}", dto);
            return;
        }
        if (dto.getIsActionLogin() == 0) {
            int ret = userStatisticDao.updateUserStatisticByAgencyIdAndRegisterDate(dto);
            if (ret == 0) {
                logger.error("用户注册统计>>>>,更新为0,改为插入,data={}", dto);
                userStatisticDao.insertUserStatisticByAgencyIdAndRegisterDate(dto);
            }
        }

        int ret1 = userActionStatisticDao.updateUserStatisticByAgencyIdAndActionDate(dto);
        if (ret1 == 0) {
            logger.error("用户行为统计>>>>,更新为0,改为插入,data={}", dto);
            userActionStatisticDao.insertUserStatisticByAgencyIdAndActionDate(dto);
        }
    }
}
