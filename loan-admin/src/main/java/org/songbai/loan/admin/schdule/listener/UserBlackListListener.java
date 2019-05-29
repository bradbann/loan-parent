package org.songbai.loan.admin.schdule.listener;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.user.dao.UserBlackListDao;
import org.songbai.loan.admin.user.dao.UserBlackListReadyDao;
import org.songbai.loan.admin.user.service.UserBlackListService;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.user.UserBlackListModel;
import org.songbai.loan.model.user.UserBlackListReadyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author: wjl
 * @date: 2018/11/20 10:30
 * Description: 用户灰名单和白名单的jms
 */
@Component
public class UserBlackListListener {
	private static final Logger log = LoggerFactory.getLogger(UserBlackListListener.class);
	@Autowired
	private UserBlackListDao blackListDao;
	@Autowired
	private UserBlackListService userBlackListService;
	@Autowired
	private UserBlackListReadyDao blackListReadyDao;
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	/**
	 * 查找所有黑名单列表中为灰白的用户，定时去更新user表和auth表
	 */
	@JmsListener(destination = JmsDest.UPDATE_BLACKLIST)
	@Transactional
	public void updateBlack() {
		log.info("执行》》》》更新用户黑名单任务《《《《开始");
		long start = System.currentTimeMillis();
		//变为灰白名单
		addBlackList();
		//移出灰白名单
		removeBlackList();
		log.info("执行》》》》更新用户黑名单任务《《《《结束，共耗时【{}】s",System.currentTimeMillis()-start);
	}

	private void addBlackList() {
		Date now = new Date();
		EntityWrapper<UserBlackListReadyModel> wrapper = new EntityWrapper<>();
		wrapper.eq("limit_start", SimpleDateFormatUtil.dateToString(now, SimpleDateFormatUtil.DATE_FORMAT2));
		List<UserBlackListReadyModel> list = blackListReadyDao.selectList(wrapper);
		list.forEach(blackListReadyModel -> {
			if (DateUtils.isSameDay(now,blackListReadyModel.getLimitStart())){
				Integer status = blackListReadyModel.getStatus();
				Integer userId = blackListReadyModel.getUserId();
				Integer agencyId = blackListReadyModel.getAgencyId();
				userBlackListService.updateUserAndAuth(userId, status);
				UserBlackListModel model = new UserBlackListModel();
				model.setUserId(userId);
				model.setAgencyId(agencyId);
				model.setType(status);
				model.setBlackFrom("平台");
				model.setLimitStart(now);
				model.setLimitEnd(blackListReadyModel.getLimitEnd());
				model.setRemark("定时任务加入黑名单列表");
				model.setOperator("定时任务");
				blackListDao.insert(model);
				redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO,userId);
			}
		});
	}

	private void removeBlackList() {
		Date now = new Date();
		EntityWrapper<UserBlackListReadyModel> wrapper = new EntityWrapper<>();
		wrapper.eq("limit_end", SimpleDateFormatUtil.dateToString(now, SimpleDateFormatUtil.DATE_FORMAT2));
		List<UserBlackListReadyModel> list = blackListReadyDao.selectList(wrapper);
		list.forEach(blackListReadyModel -> {
			if (DateUtils.isSameDay(now,blackListReadyModel.getLimitEnd())){
				Integer userId = blackListReadyModel.getUserId();
				userBlackListService.updateUserAndAuth(userId, 1);
				blackListDao.deleteById(userId);
				blackListReadyDao.deleteById(userId);
				redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO,userId);
			}
		});
	}

}
