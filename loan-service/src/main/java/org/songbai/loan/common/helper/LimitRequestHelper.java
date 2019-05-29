package org.songbai.loan.common.helper;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Author: qmw
 * Date: 2018/7/3 下午6:01
 */
@Component
public class LimitRequestHelper {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @return true 可以正常范围,false:在一定时间内请求过一次
     */
    public void validateUserRequest(String key, Integer userId, Long timeOut) {
        String redisKey = key + userId;
        if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
            throw new BusinessException(UserRespCode.REQUEST_MORE);
        }
        redisTemplate.expire(redisKey, timeOut == null ? 5 : timeOut, TimeUnit.SECONDS);
    }

    /**
     * 支付时短信验证码限制
     */
    public void payMsgLimit(String orderNum,Integer userId){
        if (!redisTemplate.opsForValue().setIfAbsent(UserRedisKey.PAYMENT_LIMIT + orderNum + userId,1)){
            Long time = redisTemplate.getExpire(UserRedisKey.PAYMENT_LIMIT + orderNum + userId);
            throw new BusinessException(UserRespCode.REQUEST_MORE,"请于"+time+"S后再次获取验证码");
        }
        redisTemplate.expire(UserRedisKey.PAYMENT_LIMIT + orderNum + userId, 55, TimeUnit.SECONDS);
    }

}
