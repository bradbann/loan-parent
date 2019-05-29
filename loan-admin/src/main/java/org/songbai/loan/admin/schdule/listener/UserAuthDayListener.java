package org.songbai.loan.admin.schdule.listener;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.user.dao.AuthenticationDao;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.user.AuthenticationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author wjl
 * @date 2018年11月09日 09:14:46
 * @description 解除授信定时任务
 */
@Component
public class UserAuthDayListener {
	private static final Logger log = LoggerFactory.getLogger(UserAuthDayListener.class);
	@Autowired
	private AuthenticationDao authDao;
	@Autowired
	private SpringProperties springProperties;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@JmsListener(destination = JmsDest.AUTH_REMAINDAYS)
	public void dealUserAuthRemainDays() {
		log.info("执行》》》》解除授信天数任务《《《《开始");
		long start = System.currentTimeMillis();
		EntityWrapper<AuthenticationModel> wrapper = new EntityWrapper<>();
		wrapper.eq("remain_days", SimpleDateFormatUtil.dateToString(new Date(), SimpleDateFormatUtil.DATE_FORMAT2));
		List<AuthenticationModel> list = authDao.selectList(wrapper);
		if (list.size() > 0) {
			list.forEach(e -> {
				AuthenticationModel authModel = new AuthenticationModel();
				authModel.setUserId(e.getUserId());
				AuthenticationModel authenticationModel = authDao.selectOne(authModel);
				authModel.setStatus(0);
				authModel.setPhoneStatus(0);
				authModel.setAlipayStatus(0);
				authModel.setMoney(authenticationModel.getMoney() - springProperties.getInteger("user.auth.alipay",80) - springProperties.getInteger("user.auth.phone",100));
				authDao.updateById(authModel);

				//删除redis中的user信息
				redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, e.getUserId());

				log.info("定时任务解除用户【{}】授信天数成功,同时删除redis信息", e.getUserId());
			});
		}
		log.info("执行》》》》解除授信天数任务《《《《结束，共耗时【{}】s", System.currentTimeMillis() - start);
	}
}
