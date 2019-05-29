package org.songbai.loan.model.finance;

import lombok.Data;

/**
 * @author: wjl
 * @date: 2018/12/20 13:06
 * Description:
 */
@Data
public class PlatformConfig extends FinancePlatformConfigModel {

	private String privateKey;//平台私钥
	private String publicKey;//畅捷公钥
	private String url;//畅捷支付请求地址
	private String sellId;//商户号
	private String expend;//拓展字段 预留
	private String agencyName;//拓展字段 预留
	private String code;//changejie,yibao

	@Override
	public String toString() {
		return "{" +
				"\"privateKey\":\"" + privateKey + "\", \"publicKey\":\"" + publicKey +
				"\", \"sellId\":\"" + sellId +
				"\", \"url\":\"" + url +
				"\"}";
	}

}
