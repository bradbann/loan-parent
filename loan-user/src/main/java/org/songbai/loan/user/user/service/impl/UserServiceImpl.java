package org.songbai.loan.user.user.service.impl;

import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.UserDao;
import org.songbai.loan.user.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户service
 *
 * @author wjl
 * @date 2018年10月30日 10:31:43
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private AuthenticationDao authDao;
	@Autowired
	private SpringProperties springProperties;


	/**
	 * 查看用户的认证状态
	 */
	@Override
	public Map<String, Integer> authInfo(Integer userId) {
		Map<String, Integer> map = new HashMap<>();
		AuthenticationModel model = authDao.selectById(userId);
		if (model == null) {
			return new HashMap<>();
		}
		map.put("idcard", springProperties.getInteger("user.auth.idcard", 300));
		map.put("face", springProperties.getInteger("user.auth.face", 200));
		map.put("info", springProperties.getInteger("user.auth.info", 300));
		map.put("phone", springProperties.getInteger("user.auth.phone", 100));
		map.put("alipay", springProperties.getInteger("user.auth.alipay", 80));
		map.put("bank", springProperties.getInteger("user.auth.bank", 20));
		map.put("idcardStatus", model.getIdcardStatus());
		map.put("faceStatus", model.getFaceStatus());
		map.put("infoStatus", model.getInfoStatus());
		map.put("phoneStatus", model.getPhoneStatus());
		map.put("alipayStatus", model.getAlipayStatus());
		map.put("bankStatus", model.getBankStatus());
		map.put("money", model.getMoney());
		map.put("status", model.getStatus());
		return map;
	}

}
