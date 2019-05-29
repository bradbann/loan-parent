package org.songbai.loan.user.user.model.vo;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 2:50 PM
 */
@Data
public class LoanDetail {
	private String loan;//借款金额
	private String stampTax;//综合费
	private String obtain;//实际到账金额(应打款)
	private Integer days;//借款期限
	private String bankName;//银卡名称
	private String bankNumber;//银卡卡号
}
