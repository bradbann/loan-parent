package org.songbai.loan.admin.order.listener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 监听超期订单数
 */
@Component
public class ExpireOrderListener {
    private static final Logger logger = LoggerFactory.getLogger(ExpireOrderListener.class);

    @Autowired
    private StatisSendHelper statisSendHelper;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ComUserService comUserService;


    @JmsListener(destination = JmsDest.ORDER_EXPIRE_STATIS)
    public void expireOrder() {
        String today = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        logger.info("expireOrder statis is start,date={}", today);
        List<OrderModel> orderList = orderDao.findOrderListByRepayMentDate(null, today);
        orderList.forEach(e -> {
            UserModel userModel = comUserService.selectUserModelById(e.getUserId());
            if (userModel == null) return;
            statisSendHelper.sendReviewStatis(e, userModel.getVestId(), OrderConstant.Stage.LOAN.key, OrderConstant.Status.SUCCESS.key, userModel.getChannelCode());
        });
        logger.info("expireOrder statis is end,listSize={}", CollectionUtils.isEmpty(orderList) ? 0 : orderList.size());
    }

}
