package org.songbai.loan.model.user;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 身份证黑名单
 * @author wjl
 * @date 2018年11月01日 17:54:48
 * @description
 */
@Data
@TableName("loan_u_blacklist_one")
public class BlackListOneModel implements Serializable{
	
	@TableId
	private Integer id;
	private String name;//姓名
	private String idcardNum;//身份证号
	private String blackFrom;//黑名单来源
	private Date createTime;
	private Date updateTime;
}
