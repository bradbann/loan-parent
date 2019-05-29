package org.songbai.loan.model.finance;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 银行卡model
 * @author wjl
 * @date 2018年11月09日 10:58:31
 * @description
 */
@Data
@TableName("loan_u_finance_bank")
public class FinanceBankModel {
	
	@TableId
	private Integer id;
	private String icon;//银行图标
	private String bankCode;//平台银行卡代码
	private String thirdBankCode;//第三方支付公司银行卡代码
	private Integer payPlatformId;//支付平台的id（1 畅捷，2 易宝）
	private String bankName;//银行名字
	private String bankArea;//银行区域 1大陆 2台湾
	private Integer sort;//排序
	private Integer status;//1可用 0不可用
	private Date createTime;
	private Date updateTime;
}
