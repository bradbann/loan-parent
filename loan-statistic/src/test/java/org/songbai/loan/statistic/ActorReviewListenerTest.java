package org.songbai.loan.statistic;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.statis.ChannelStatisConst;
import org.songbai.loan.constant.user.OrderConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActorReviewListenerTest {
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    RedisTemplate<Object, String> redisTemplate;

    @Test
    public void channelStatis() {
        JSONObject json = new JSONObject();
        json.put("agencyId", 0);
        json.put("channelId", 1);
        json.put("type", ChannelStatisConst.ChannelStatisType.REGISTER_LOGIN.key);

        jmsTemplate.convertAndSend(JmsDest.CHANNEL_STATIS, json);
    }

    @Test
    public void reviewStatis() {
        JSONObject json = new JSONObject();
        json.put("stage", OrderConstant.Stage.ARTIFICIAL_AUTH.key);
        json.put("agencyId", 0);
        json.put("guest", 3);
        json.put("status", OrderConstant.Status.SUCCESS.key);
        json.put("calcDate", new Date());
        jmsTemplate.convertAndSend(JmsDest.REVIEW_STATIS, json);
    }

    @Test
    public void channelDeduc() throws InterruptedException {

//        UserModel userModel = new UserModel();
//        userModel.setAgencyId(18);
//        userModel.setChannelId(7);
//        userModel.setPhone("18969926654");
//        for (int i = 0; i < 100; i++) {
//            Thread.sleep(10);
//            userModel.setId(i);
//            jmsTemplate.convertAndSend(JmsDest.CHANNEL_DEDUCTION_STATIS, userModel);
//        }
//        Thread.sleep(100000);

    }


}
