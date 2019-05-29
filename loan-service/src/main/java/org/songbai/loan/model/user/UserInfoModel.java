package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户【个人信息认证】类
 * @author wjl
 * @date 2018年10月29日 15:04:27
 * @description
 */
@Data
@TableName("loan_u_user_info")
public class UserInfoModel implements Serializable{

	private static final long serialVersionUID = 1L;

	@TableId(type=IdType.INPUT)
	private Integer userId;
	private Integer agencyId;
	private Integer sex;//性别  1男 2女
	private String name;//真实姓名
	private String idcardNum;//身份证号
	private String livingImg ; // 活体照片
	private String idcardFrontImg;//身份证正面照片
	private String idcardBackImg;//身份证背面照片
	private String validation;//身份证有效期
	private String idcardAddress;//身份证住址
	private String address;//现居地址
	private String education;//学历
	private String addressTime;//居住时间
	private String marry;//是否结婚
	private String job;//工作
	private String jobName;//工作单位
	private String companyAddress;//公司地址
//	private String firstRela;//直接联系人关系
	private String firstContact;//紧急联系人
	private String firstPhone;//紧急联系人电话
//	private String otherRela;//间接联系人关系
	private String otherContact;//备用联系人
	private String otherPhone;//备用联系人电话
	private Date createTime;
	private Date updateTime;
    private Integer deleted;//0未删除 1已删除
}
