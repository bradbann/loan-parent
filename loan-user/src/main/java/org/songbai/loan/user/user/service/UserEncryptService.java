package org.songbai.loan.user.user.service;

import org.songbai.loan.model.user.UserModel;
import org.springframework.stereotype.Component;

@Component
public interface UserEncryptService {
	UserModel encrypt(String userPass);
	//UserModel encryptSecurityPass(String withDrawPass);
}
