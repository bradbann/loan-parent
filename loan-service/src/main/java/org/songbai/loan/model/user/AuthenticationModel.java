package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户所有认证model
 * @author wjl
 * @date 2018年10月31日 10:37:15
 * @description
 */
@Data
@TableName("loan_u_authentication")
public class AuthenticationModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@TableId(type=IdType.INPUT)
	private Integer userId;
	private Integer agencyId;
	private Integer status;//是否都认证通过 0 否 1是
	private Integer type;//用户状态0黑名单,1正常 2灰名单 3白名单
	private Integer idcardStatus;//实名认证状态	（0未认证 1认证成功2认证失败）
	private Integer faceStatus;//人脸识别状态	（0未认证 1认证成功2认证失败）
	private Integer infoStatus;//个人信息状态	（0未认证 1认证成功2认证失败）
	private Integer phoneStatus;//手机认证状态	（0未认证 1认证成功2认证失败）
	private Integer alipayStatus;//支付宝认证状态	（0未认证 1认证成功2认证失败）
	private Integer bankStatus;//银行卡认证状态	（0未认证 1认证成功2认证失败）
	private Date idcardTime;//更新时间
	private Date faceTime;//更新时间
	private Date infoTime;//更新时间
	private Date phoneTime;//更新时间
	private Date alipayTime;//更新时间
	private Date bankTime;//更新时间
	private Integer money;//认证所得额度（整数）
	private Date remainDays;//授信天数到期日期
	private Date createTime;//创建时间
    private Date updateTime;//更新时间
}
