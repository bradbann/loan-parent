/**  
 * Project Name:api-webhook  
 * File Name:BillDetail.java  
 * Package Name:org.songbai.loan.risk.moxie.carrier.dto
 * Date:2016年7月25日下午4:44:23  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.carrier.dto;

import java.util.ArrayList;
import java.util.List;

/**  
 * ClassName:BillDetail <br/>  
 * Date:     2016年7月25日 下午4:44:23 <br/>

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
public class BillDetail {
	private String mobile;
	private List<MobileBill> bills = new ArrayList<>();
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public List<MobileBill> getBills() {
		return bills;
	}
	public void setBills(List<MobileBill> bills) {
		this.bills = bills;
	}

}
  
