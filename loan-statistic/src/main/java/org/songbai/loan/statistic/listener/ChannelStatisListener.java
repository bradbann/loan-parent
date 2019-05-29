package org.songbai.loan.statistic.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.statis.ChannelStatisConst.ChannelStatisType;
import org.songbai.loan.model.statistic.ChannelStatisModel;
import org.songbai.loan.statistic.dao.ChannelStatisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

/**
 * 渠道统计
 */
@Component
public class ChannelStatisListener {
    private static final Logger logger = LoggerFactory.getLogger(ChannelStatisListener.class);

    @Autowired
    ChannelStatisDao channelStatisDao;

    @JmsListener(destination = JmsDest.CHANNEL_STATIS)
    public void channelStatis(JSONObject json) {
        logger.info(">>>>channelStatis recive msg={}", json);
        if (json == null || json.get("channelId") == null || json.get("agencyId") == null || json.get("type") == null) {
            logger.error(">>>>channelStatis info is error,msg={}", json);
            return;
        }
        Integer type = json.getInteger("type");
        Integer agencyId = json.getInteger("agencyId");
        Integer channelId = json.getInteger("channelId");
        LocalDate calcDate = Date8Util.date2LocalDate(new Date());
        ChannelStatisModel model = channelStatisDao.getInfoByChannelId(agencyId, channelId, calcDate);
        ChannelStatisModel param = new ChannelStatisModel();

        if (type.equals(ChannelStatisType.REGISTER.key)) param.setRegisterCount(1);
        else if (type.equals(ChannelStatisType.LOGIN.key)) param.setLoginCount(1);
        else if (type.equals(ChannelStatisType.ORDER.key)) param.setOrderCount(1);
        else if (type.equals(ChannelStatisType.TRANSFER.key)) param.setTransferCount(1);
        else if (type.equals(ChannelStatisType.REGISTER_LOGIN.key)) {
            param.setRegisterCount(1);
            param.setLoginCount(1);
        }
        if (model == null) {
            param.setChannelId(channelId);
            param.setAgencyId(agencyId);
            param.setCalcDate(calcDate);
            channelStatisDao.insert(param);
            return;
        }
        param.setId(model.getId());
        channelStatisDao.updateChannelStatis(param);
    }

}

