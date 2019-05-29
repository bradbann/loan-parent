/**  
 * Project Name:api-webhook  
 * File Name:FamilyNetDetail.java  
 * Package Name:org.songbai.loan.risk.moxie.carrier.dto
 * Date:2016年7月25日下午10:16:31  
 * Copyright (c) 2016, yuandong@51dojo.com All Rights Reserved.  
 *  
*/  
  
package org.songbai.loan.risk.moxie.carrier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**  
 * ClassName:FamilyNetDetail <br/>  
 * Date:     2016年7月25日 下午10:16:31 <br/>

 * @version    
 * @since    JDK 1.6  
 * @see        
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FamilyNetDetail {
	//private String mobile;
    private List<FamilyNet> families = new ArrayList<>();

//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }

    public List<FamilyNet> getFamilies() {
        return families;
    }

    public void setFamilies(List<FamilyNet> families) {
        this.families = families;
    }
}
  
