package org.songbai.loan.model.finance;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

import java.util.Date;

/**
 * 支付平台model
 * @author wjl
 * @date 2018年11月09日 10:59:09
 * @description
 */
@Data
@TableName("loan_u_finance_platform")
public class FinancePlatformModel {
	
	@TableId
	private Integer id;
	private String code;
	private String name;
	private Integer status;//0 禁用 1启用
	/**
	 * 1 支付宝 2 微信 3 网上银行
	 */
	private String payType;
	private Date createTime;
	private Date updateTime;
}
