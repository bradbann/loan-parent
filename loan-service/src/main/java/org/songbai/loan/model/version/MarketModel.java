package org.songbai.loan.model.version;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("loan_u_market")
public class MarketModel {

	private Integer id;
	private String marketKey;//渠道key
	private String marketName;//渠道name
	private Date createTime;
	private Date updateTime;
}
