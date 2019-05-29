/**  
 * Project Name:alipay-worker-server  
 * File Name:DeliverAddress.java  
 * Package Name:com.moxie.cloud.services.alipayworker.model.taobao
 * Date:2016年6月13日下午4:47:34  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**  
 * ClassName:DeliverAddress <br/>  
 * Date:     2016年6月13日 下午4:47:34 <br/>
 * @version
 * @since    JDK 1.6
 * Modified by liyang on 20171019 接口升级
 * @see
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeliverAddress {
	@JsonProperty("mapping_id")
	private String mappingId;
	private String name;
	private String address;
	@JsonProperty("full_address")
	private String fullAddress;
	@JsonProperty("zip_code")
	private String zipCode;
	@JsonProperty("phone_no")
	private String phoneNumber;

	/**
	 * 说明：20171019接口升级，增加@JsonProperty("default")
	 */
	@JsonProperty("default")
	private boolean isDefault;

	private String province;
	private String city;
	
	public String getMappingId() {
		return mappingId;
	}
	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFullAddress() {
		return fullAddress;
	}
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
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
  
