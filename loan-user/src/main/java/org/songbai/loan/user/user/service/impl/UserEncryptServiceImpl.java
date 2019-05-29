package org.songbai.loan.user.user.service.impl;

import org.songbai.cloud.basics.encrypt.PasswordEncryptUtil;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.user.user.service.UserEncryptService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserEncryptServiceImpl implements UserEncryptService {
	private static final Integer SALT_SIZE = 8;

	private String generateSalt() {
		String s = "";
		Random r = new Random();
		for (int i = 0; i < SALT_SIZE; i++) {
			s += (char) (48 + r.nextInt(43));
		}
		return s.toLowerCase();
	}

	@Override
	public UserModel encrypt(String userPass) {
		UserModel userModel = new UserModel();
		String salt = generateSalt();
		userModel.setPassSalt(salt);
		int encryptTimes = (int) (1 + Math.random() * 4);
		userModel.setPassEncryptTimes(encryptTimes);
		String userPassEncrypt = PasswordEncryptUtil.digest(userPass, salt, encryptTimes);
		userModel.setUserPass(userPassEncrypt);
		return userModel;
	}

	//@Override
	//public UserModel encryptSecurityPass(String withDrawPass) {
	//	UserModel userModel = new UserModel();
	//	String salt = generateSalt();
	//	userModel.setDrawPassSalt(salt);
	//	int encryptTimes = (int) (1 + Math.random() * 4);
	//	userModel.setDrawPassEncryptTimes(encryptTimes);
	//	String drawPassEncrypt = PasswordEncryptUtil.digest(withDrawPass, salt, encryptTimes);
	//	userModel.setWithDrawPass(drawPassEncrypt);
	//	return userModel;
	//}

}
