/**  
 * Project Name:api-webhook  
 * File Name:MobileSmsDetail.java  
 * Package Name:org.songbai.loan.risk.moxie.carrier.dto
 * Date:2016年7月25日下午6:28:02  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.carrier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**  
 * ClassName:MobileSmsDetail <br/>  
 * Function: . <br/>
 * Reason:   . <br/>
 * Date:     2016年7月25日 下午6:28:02 <br/>  

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MobileSmsDetail {

	private String billMonth;
	private String code;
	private String message;
	@JsonProperty("total_size")
	private Integer totalSize;
	private List<MobileSms> records = new ArrayList<>();

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

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

	public Integer getTotalSize() {
			return totalSize;
		}
	public void setTotalSize(Integer totalSize) {
			this.totalSize = totalSize;
		}
	public List<MobileSms> getRecords() {
			return records;
		}
	public void setRecords(List<MobileSms> records) {
			this.records = records;
		}
	    
	    

}
  
