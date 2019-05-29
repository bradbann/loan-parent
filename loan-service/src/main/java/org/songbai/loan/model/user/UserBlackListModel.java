package org.songbai.loan.model.user;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/**
 * 黑名单用户
 * @author wjl
 * @date 2018年10月30日 15:58:14
 * @description
 */
@Data
@TableName("loan_u_user_blacklist")
public class UserBlackListModel {
	
	@TableId(type=IdType.INPUT)
	private Integer userId;
	private Integer agencyId;
	private Integer type;//0 黑名单 2灰名单 3白名单
	private String blackFrom;//黑名单来源
	private Date limitStart;//黑、灰名单限制时间
	private Date limitEnd;//黑、灰名单限制时间
	private String remark;//备注
	private String operator;//操作人
	private Date createTime;
	private Date updateTime;
}
