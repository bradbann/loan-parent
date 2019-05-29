/**  
 * Project Name:api-webhook  
 * File Name:TaobaoTask.java  
 * Package Name:com.example.service.webhook.business.taobao.billitem  
 * Date:2016年8月11日下午9:16:59  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**  
 * ClassName:TaobaoTask <br/>  
 * Date:     2016年8月11日 下午9:16:59 <br/>

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
public class TaobaoTask {
	@JsonProperty("task_id")
	private String taskId;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("mapping_id")
	private String mappingId;
	private String type;
	private String account;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMappingId() {
		return mappingId;
	}
	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	

}
  
