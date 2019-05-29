package org.songbai.loan.user.user.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/26 4:23 PM
 */
@Data
public class UserVO {

	private String name;//姓名
	private String phone;//手机号码
	private Date lastLoginTime;//最后登陆时间
	private String mobileName;//手机品牌
	private String mobileType;//手机型号
	private String loginIp;//登录ip


	private Integer sex;//性别  1男 2女

	public String getPhone() {
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}
}
