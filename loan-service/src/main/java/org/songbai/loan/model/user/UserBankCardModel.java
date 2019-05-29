package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户【银行卡认证】类
 * @author wjl
 * @date 2018年10月29日 20:48:08
 * @description
 */
@Data
@TableName("loan_u_user_bankcard")
public class UserBankCardModel implements Serializable{
	
	@TableId
	private Integer id;
	private Integer userId;
	private Integer agencyId;
	private String requestId;//请求的id，相当于绑卡订单号，可以根据此查绑卡信息
	private String name;//姓名
	private String icon;//银行图标
	private String bankName;//银行名称
	private String bankCode;//平台的银行卡code
	private String bankCardNum;//银行卡号
	private Integer bankCardType;//银行卡类型，0:不能识别; 1: 借记卡; 2: 信用卡
	private String bankPhone;//银行卡预留手机号
	private String bindPlatform;//绑定的支付平台code
	private Integer type;//0非默认 1默认
	private Integer status;//0待绑定  1已绑定 2 已解绑
	private String city;//开户行所在城市
	private String province;//开户行所在省
	private String branchBankName;//分行名称
	private Date createTime;
	private Date updateTime;

    private Integer deleted;//0未删除 1已删除
	
}
