package org.songbai.loan.user.user.model.po;


import lombok.Data;


@Data
public class UserRegisterPO {

	private String phone;
	private String email;

	private String msgCode;
	private String imgCode;
	private String userPass;

	private String deviceId;//设备号
	private String source;
	private String ip;
	private Integer agencyId;//代理id

	private String gexing;//个推id
	private String mobileName;//手机品牌
	private String mobileType;//手机型号
	private String systemVersion;//系统版本
	private String landCode;//渠道编号
    private String market;//
    private String platform;

}
