/**  
 * Project Name:alipay-worker-server  
 * File Name:TaobaoSubOrder.java  
 * Package Name:com.moxie.cloud.services.alipayworker.dto.taobao  
 * Date:2016年6月12日下午8:55:08  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**  
 * ClassName:TaobaoSubOrder <br/>  
 * Function: 淘宝交易下面的子订单（商品明细） <br/>  
 * Reason:   . <br/>
 * Date:     2016年6月12日 下午8:55:08 <br/>  

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaobaoSubOrder {
	@JsonProperty("mapping_id")
	private String mappingId;
	@JsonProperty("trade_id")
	private String tradeId;
	@JsonProperty("item_id")
	private String itemId;
	@JsonProperty("item_url")
	private String itemUrl;
	@JsonProperty("item_pic")
	private String itemPic;
	@JsonProperty("item_name")
	private String itemName;
	@JsonProperty("original")
	private int original;
	@JsonProperty("real_total")
	private int realTotal;
	
	private Integer quantity;

	@JsonProperty("cid_level1")
	private String cidLevel1;

	@JsonProperty("cid_level2")
	private String cidLevel2;

	@JsonProperty("cname_level1")
	private String cnameLevel1;

	@JsonProperty("cname_level2")
	private String cnameLevel2;

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
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemUrl() {
		return itemUrl;
	}
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	public String getItemPic() {
		return itemPic;
	}
	public void setItemPic(String itemPic) {
		this.itemPic = itemPic;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public String getCidLevel1() {
		return cidLevel1;
	}

	public void setCidLevel1(String cidLevel1) {
		this.cidLevel1 = cidLevel1;
	}

	public String getCidLevel2() {
		return cidLevel2;
	}

	public void setCidLevel2(String cidLevel2) {
		this.cidLevel2 = cidLevel2;
	}

	public String getCnameLevel1() {
		return cnameLevel1;
	}

	public void setCnameLevel1(String cnameLevel1) {
		this.cnameLevel1 = cnameLevel1;
	}

	public String getCnameLevel2() {
		return cnameLevel2;
	}

	public void setCnameLevel2(String cnameLevel2) {
		this.cnameLevel2 = cnameLevel2;
	}

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}

	public int getRealTotal() {
		return realTotal;
	}

	public void setRealTotal(int realTotal) {
		this.realTotal = realTotal;
	}
}
  
