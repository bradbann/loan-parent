package org.songbai.loan.model.user;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 手机号黑名单
 * @author wjl
 * @date 2018年11月01日 17:54:48
 * @description
 */
@Data
@TableName("loan_u_blacklist_two")
public class BlackListTwoModel implements Serializable{
	
	@TableId
	private Integer id;
	private String phone;//手机号
	private String blackFrom;//黑名单来源
	private Date createTime;
	private Date updateTime;
}
