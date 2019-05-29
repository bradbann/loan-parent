package org.songbai.loan.service.user.service.impl;

import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.songbai.loan.model.loan.ProductModel;
import org.songbai.loan.service.user.dao.ComProductDao;
import org.songbai.loan.service.user.service.ComProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ComProductServiceImpl implements ComProductService {
    @Autowired
    ComProductDao comProductDao;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public ProductModel getProductInfoById(Integer productId) {
        ProductModel model = (ProductModel) redisTemplate.opsForHash().get(UserRedisKey.USER_PRODUCT, productId);
        if (model == null) {
            model = comProductDao.getProductInfoById(productId);
            if (model != null)
                redisTemplate.opsForHash().put(UserRedisKey.USER_PRODUCT, productId, model);
        }
        return model;
    }

    @Override
    public ProductGroupModel getProductGroupByGroupId(Integer groupId) {
        ProductGroupModel groupModel = (ProductGroupModel) redisTemplate.opsForHash().get(UserRedisKey.USER_PRODUCT_GROUP, groupId);
        if (groupModel == null ){
            groupModel = comProductDao.getProductGroupByGroupId(groupId);
            if (groupModel != null ){
                redisTemplate.opsForHash().put(UserRedisKey.USER_PRODUCT_GROUP, groupId, groupModel);
            }
        }
        return groupModel;
    }
}
