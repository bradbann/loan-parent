package org.songbai.loan.statistic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.common.util.PhoneUtil;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.user.ChannelUserModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.statistic.dao.ChannelDao;
import org.songbai.loan.statistic.dao.ChannelUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DeductionListener {
    private static final Logger logger = LoggerFactory.getLogger(ActorOrderStatisListener.class);
    @Autowired
    ChannelDao channelDao;
    @Autowired
    RedisTemplate<Object, String> redisTemplate;
    @Autowired
    ChannelUserDao channelUserDao;


    @JmsListener(destination = JmsDest.CHANNEL_DEDUCTION_STATIS)
    public void channelDeduction(UserModel userModel) {
        Integer agencyId = userModel.getAgencyId();
        Integer channelId = userModel.getChannelId();
        AgencyChannelModel channelModel = channelDao.getInfoByIdAndAgencyId(channelId, agencyId);
        logger.info(">>>>channelDeduction is start,userPhone={}", userModel.getPhone());
        if (channelModel == null) {
            logger.info(">>>> channelDeduction channelModel is not exisit,channelId={},agencyId={}", channelId, agencyId);
            return;
        }
        Double showPercent = channelModel.getShowPercent() == null ? 100D : channelModel.getShowPercent();

        //剩余未统计数量
        Integer totalCount = 100;
        //剩余未移除数量
        Integer removeCount = (int) Math.rint(100 - showPercent.intValue());

        if (redisTemplate.opsForHash().hasKey(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId)) {
            totalCount = (Integer) redisTemplate.opsForHash().get(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId);
        } else {
            redisTemplate.opsForHash().put(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId, 100);
            redisTemplate.opsForHash().put(UserRedisKey.USER_CHANNEL_STATIS_DEDUCA + agencyId, channelId, removeCount);
        }

        removeCount = (Integer) redisTemplate.opsForHash().get(UserRedisKey.USER_CHANNEL_STATIS_DEDUCA + agencyId, channelId);

        Integer randowmNum = (int) (Math.random() * 100);
        if (removeCount >= totalCount || (randowmNum >= showPercent && removeCount > 0)) {//过滤
            redisTemplate.opsForHash().increment(UserRedisKey.USER_CHANNEL_STATIS_DEDUCA + agencyId, channelId, -1);
        } else {
            ChannelUserModel model = new ChannelUserModel();
            model.setAgencyId(userModel.getAgencyId());
            model.setChannelId(userModel.getChannelId());
            model.setUserId(userModel.getId());

            model.setUserPhone(PhoneUtil.mobileEncrypt(userModel.getPhone()));
            channelUserDao.insert(model);
        }


        redisTemplate.opsForHash().increment(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId, -1);
        Integer lastCount = (Integer) redisTemplate.opsForHash().get(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId);
        if (lastCount == 0) {
            redisTemplate.opsForHash().delete(UserRedisKey.USER_CHANNEL_STATIS_LIST + agencyId, channelId);
            redisTemplate.opsForHash().delete(UserRedisKey.USER_CHANNEL_STATIS_DEDUCA + agencyId, channelId);
        }

    }

}
