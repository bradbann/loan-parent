/**  
 * Project Name:ofs-service-common  
 * File Name:TradeDetails.java  
 * Package Name:com.moxie.cloud.services.ofs.common.taobao.dto  
 * Date:2016年6月14日下午9:36:31  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**  
 * ClassName:TradeDetails <br/>  
 * Function: . <br/>
 * Reason:   . <br/>
 * Date:     2016年6月14日 下午9:36:31 <br/>  

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeDetails {

	@JsonProperty("mapping_id")
	private String mappingId;
	@JsonProperty("trade_id")
	private String tradeId;
	@JsonProperty("trade_status")
	private String tradeStatus;
	@JsonProperty("trade_createtime")
	private Date tradeCreateTime;
	@JsonProperty("actual_fee")
	private int actualFee;
	@JsonProperty("seller_id")
	private long sellerId;
	@JsonProperty("seller_nick")
	private String sellerNick;
	@JsonProperty("seller_shopname")
	private String sellerShopName;
	@JsonProperty("trade_text")
	private String tradeText;
	
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
	@JsonProperty("deliver_fulladdress")
	private String deliverFullAddress;//全量地址
	@JsonProperty("invoice_name")
	private String invoiceName;// 发票抬头
	@JsonProperty("sub_orders")
	private List<TaobaoSubOrder> subOrders;

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
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

	public long getSellerId() {
		return sellerId;
	}

	public void setSellerId(long sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerNick() {
		return sellerNick;
	}

	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}

	public String getSellerShopName() {
		return sellerShopName;
	}

	public void setSellerShopName(String sellerShopName) {
		this.sellerShopName = sellerShopName;
	}

	public String getTradeText() {
		return tradeText;
	}

	public void setTradeText(String tradeText) {
		this.tradeText = tradeText;
	}

	public List<TaobaoSubOrder> getSubOrders() {
		return subOrders;
	}

	public void setSubOrders(List<TaobaoSubOrder> subOrders) {
		this.subOrders = subOrders;
	}

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

	public String getDeliverFullAddress() {
		return deliverFullAddress;
	}

	public void setDeliverFullAddress(String deliverFullAddress) {
		this.deliverFullAddress = deliverFullAddress;
	}

	public String getInvoiceName() {
		return invoiceName;
	}

	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}
	
	

}
  
