/**  
 * Project Name:api-webhook  
 * File Name:MobileRecharge.java  
 * Package Name:org.songbai.loan.risk.moxie.carrier.dto
 * Date:2016年7月25日下午9:17:43  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.carrier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**  
 * ClassName:MobileRecharge <br/>  
 * Function: 充值记录明细 <br/>  
 * Reason:   . <br/>
 * Date:     2016年7月25日 下午9:17:43 <br/>  

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MobileRecharge {
	private String code;

	private String message;

	private List<MobileRechargeItem> items = new ArrayList<>();

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<MobileRechargeItem> getItems() {
		return items;
	}

	public void setItems(List<MobileRechargeItem> items) {
		this.items = items;
	}
}
  
