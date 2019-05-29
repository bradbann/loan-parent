package org.songbai.loan.user.user.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/1 5:06 PM
 */
@Data
public class OrderListVO {
	private String orderNumber;//订单号
	private Double loan;//借款金额
	private Double payment;//应还金额
	private Integer stage;//阶段
	private Integer status;//状态
	private String statusName;//状态
	private Date createTime;//下单时间
}
