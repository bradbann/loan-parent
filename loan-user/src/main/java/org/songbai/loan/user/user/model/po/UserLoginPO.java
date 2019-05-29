package org.songbai.loan.user.user.model.po;

import lombok.Data;

@Data
public class UserLoginPO {

	private String phone;

	private String userPass;

	private String deviceId;
	private String platform;

	private String mobileName;//手机品牌
	private String mobileType;//手机型号
	private String systemVersion;//系统版本
	private String appVersion;//应用版本
	private String gexing;//
	private String market;//


	private String ip;
	private Integer agencyId;//代理id


}
