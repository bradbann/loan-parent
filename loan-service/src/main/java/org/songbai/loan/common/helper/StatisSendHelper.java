package org.songbai.loan.common.helper;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.loan.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatisSendHelper {
    private static final Logger logger = LoggerFactory.getLogger(StatisSendHelper.class);

    @Autowired
    JmsTemplate jmsTemplate;

    public void sendReviewStatis(OrderModel orderModel, Integer vestId, Integer stage, Integer status, String channelCode) {
        JSONObject orderJson = new JSONObject();
        orderJson.put("agencyId", orderModel.getAgencyId());
        orderJson.put("guest", orderModel.getGuest());
        orderJson.put("stage", stage);
        orderJson.put("status", status);
        orderJson.put("calcDate", orderModel.getCreateTime());
        orderJson.put("vestId", vestId);
        orderJson.put("productId", orderModel.getProductId());
        orderJson.put("productGroupId", orderModel.getGroupId());

        if (StringUtils.isNotBlank(channelCode))
            orderJson.put("channelCode", channelCode);

        //一定要放最后一个参数
        if (orderModel.getReviewId() != null) {
            orderJson.put("actorId", orderModel.getReviewId());
            jmsTemplate.convertAndSend(JmsDest.REVIEW_STATIS, orderJson);
        }

        jmsTemplate.convertAndSend(JmsDest.REVIEW_STATIS, orderJson);
        logger.info(">>>>reviewStatis,actorReview={},orderJson={}", orderModel.getReviewId() != null, orderJson);
    }
}
