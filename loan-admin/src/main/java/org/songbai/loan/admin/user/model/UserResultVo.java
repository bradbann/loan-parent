package org.songbai.loan.admin.user.model;

import lombok.Data;
import org.songbai.loan.model.user.UserModel;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户多表查询返回值类
 * @author wjl
 * @date 2018年10月31日 13:51:16
 * @description
 */
@Data
public class UserResultVo extends UserModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//用户认证相关信息
	private Integer idcardStatus;
	private Integer faceStatus;
	private Integer infoStatus;
	private Integer phoneStatus;
	private Integer alipayStatus;
	private Integer bankStatus;
	private Integer money;
	private Integer remainDays;
	
	//用户个人信息认证信息
	private String idcardNum;
	
	//黑名单表
	private String blackFrom;
	private Date limitStart;
	private Date limitEnd;
	
	//代理名称
	private String agencyName;
	//渠道名称
	private String channelName;
	String vestName;//马甲名称
}
