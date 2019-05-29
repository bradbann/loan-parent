/**  
 * Project Name:ofs-service-common  
 * File Name:TradeDetailsRes.java  
 * Package Name:com.moxie.cloud.services.ofs.common.taobao.dto  
 * Date:2016年6月17日下午5:51:57  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**  
 * ClassName:TradeDetailsRes <br/>  
 * Function: . <br/>
 * Reason:   . <br/>
 * Date:     2016年6月17日 下午5:51:57 <br/>  

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TradeDetailsRes {
	@JsonProperty("total_size")
	private int totalSize;
	private int size;
	
	private List<TradeDetails> tradedetails = new ArrayList<TradeDetails>();

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<TradeDetails> getTradedetails() {
		return tradedetails;
	}

	public void setTradedetails(List<TradeDetails> tradedetails) {
		this.tradedetails = tradedetails;
	}

}
  
