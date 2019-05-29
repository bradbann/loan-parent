package org.songbai.loan.user.user.listener;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.user.UserDeviceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Author: qmw
 */
@Component
public class InsertUserDeviceListener {

	private static final Logger logger = LoggerFactory.getLogger(InsertUserDeviceListener.class);
	@Autowired
	private MongoTemplate mongoTemplate;

	@JmsListener(destination = JmsDest.INSERT_DEVICE)
	public void insertUserDevice(JSONObject jsonObject) {
		if (logger.isInfoEnabled()) {
			logger.info("begin create user info table,data={}", jsonObject);
		}
		Integer userId = jsonObject.getInteger("userId");
		String device = jsonObject.getString("device");
		if (userId == null || StringUtil.isEmpty(device)) {
			logger.info("receive msg insert user device  is null");
			return;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("device").is(userId));

		UserDeviceModel one = mongoTemplate.findOne(query, UserDeviceModel.class);
		if (one == null) {
			UserDeviceModel insert = new UserDeviceModel();
			insert.setDevice(device);
			insert.setUserId(userId);
			mongoTemplate.save(insert);
			logger.info("插入用户正在使用的设备号,userId={},设备id={}", userId, device);
		}
	}
}
