package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**  
 * @version
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RecentDeliverAddress {
	
	
	@JsonProperty("trade_id")
	private String tradeId;
	@JsonProperty("trade_createtime")
	private Date tradeCreateTime;
	@JsonProperty("actual_fee")
	private int actualFee;
	@JsonProperty("deliver_name")
	private String deliverName;       //姓名
	@JsonProperty("deliver_mobilephone")
	private String deliverMobilePhone;//移动电话
	@JsonProperty("deliver_fixedphone")
	private String deliverFixedPhone; //固定电话
	@JsonProperty("deliver_address")
	private String deliverAddress;    //收货地址
	@JsonProperty("deliver_postcode")
	private String deliverPostCode;   //邮编 
	@JsonProperty("invoice_name")
	private String invoiceName;// 发票抬头
	private String province;
	private String city;
	
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getDeliverMobilePhone() {
		return deliverMobilePhone;
	}
	public void setDeliverMobilePhone(String deliverMobilePhone) {
		this.deliverMobilePhone = deliverMobilePhone;
	}
	public String getDeliverFixedPhone() {
		return deliverFixedPhone;
	}
	public void setDeliverFixedPhone(String deliverFixedPhone) {
		this.deliverFixedPhone = deliverFixedPhone;
	}
	public String getDeliverAddress() {
		return deliverAddress;
	}
	public void setDeliverAddress(String deliverAddress) {
		this.deliverAddress = deliverAddress;
	}
	public String getDeliverPostCode() {
		return deliverPostCode;
	}
	public void setDeliverPostCode(String deliverPostCode) {
		this.deliverPostCode = deliverPostCode;
	}
	public String getInvoiceName() {
		return invoiceName;
	}
	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public Date getTradeCreateTime() {
		return tradeCreateTime;
	}
	public void setTradeCreateTime(Date tradeCreateTime) {
		this.tradeCreateTime = tradeCreateTime;
	}

	public int getActualFee() {
		return actualFee;
	}

	public void setActualFee(int actualFee) {
		this.actualFee = actualFee;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	

}
  
