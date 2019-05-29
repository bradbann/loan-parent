package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * @author: wjl
 * @date: 2018/11/28 23:00
 * Description: 用户准黑名单表
 */
@Data
@TableName("loan_u_user_blacklist_ready")
public class UserBlackListReadyModel {

	@TableId(type = IdType.INPUT)
	private Integer userId;
	private Integer agencyId;
	private Integer status;//0 黑 2 灰 3白
	private Date limitStart;
	private Date limitEnd;
}
