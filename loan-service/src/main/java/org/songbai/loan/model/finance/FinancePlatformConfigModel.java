package org.songbai.loan.model.finance;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: wjl
 * @date: 2018/12/10 11:40
 * Description: 支付平台配置表
 */
@Data
@TableName("loan_u_finance_platform_config")
public class FinancePlatformConfigModel {

	@TableId
	private Integer id;
	private Integer agencyId;//代理id
	private Integer platformId;//支付平台id
	private String param;//参数
	private Integer bind;//0 不支持绑卡 1支持绑卡
	private Integer status;//0禁用 1启用
	private Integer type;//1支付参数，2转账参数，3转账支付通用
	private Date createTime;
	private Date updateTime;
}
