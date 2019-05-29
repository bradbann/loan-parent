package org.songbai.loan.user.user.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/1 5:06 PM
 */
@Data
public class OrderDetailVO {

	private String loan;//借款金额
	private String stampTax;//综合费
	private Integer status;//

	private String obtain;//实际到账金额(应打款)
	private Integer exceedDays;//逾期天数
	private String exceedFee;//逾期费用

	private String payment;//应还金额
	private String alreadyMoney;//已还金额
	private String deductMoney;//减免金额

	private Integer days;//借款期限

	private String bankName;//银卡名称
	private String bankNumber;//银卡卡号

	private Date repaymentDate;//还款日期
	private Date repaymentTime;//实际还款时间
}
