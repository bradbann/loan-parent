package org.songbai.loan.user.user.controller;

import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户controller
 *
 * @author wjl
 * @date 2018年10月30日 10:33:31
 * @description
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	ComUserService comUserService;
	@Autowired
	SpringProperties properties;

	/**
	 * 我的资料
	 *
	 * @return
	 */
	@GetMapping("/authInfo")
	public Response authInfo() {
		return Response.success(userService.authInfo(UserUtil.getUserId()));
	}


	@GetMapping("/moxieConfig")
	public Response getMoxieConfig() {
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		UserInfoModel infoModel = comUserService.findUserInfoByUserId(userId);
		Map<String, Object> result = new HashMap<>();
		result.put("userId", userModel.getThirdId());
		result.put("name", infoModel.getName());
		result.put("idcard", infoModel.getIdcardNum());
		result.put("phone", userModel.getPhone());
		result.put("appkey", properties.getString("moxie.api.appkey"));
		result.put("apptoken", properties.getString("moxie.api.token"));


		return Response.success(result);
	}


}
