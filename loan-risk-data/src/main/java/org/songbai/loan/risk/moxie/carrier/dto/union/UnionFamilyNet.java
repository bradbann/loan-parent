package org.songbai.loan.risk.moxie.carrier.dto.union;

import org.songbai.loan.risk.moxie.carrier.dto.FamilyMember;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yajun on 12/5/16.
 */
public class UnionFamilyNet {

    @JsonProperty("family_num")
    private String familyNetNum;

    @JsonProperty("items")
    private List<FamilyMember> familyMembers = new ArrayList<>();

	public String getFamilyNetNum() {
		return familyNetNum;
	}

	public void setFamilyNetNum(String familyNetNum) {
		this.familyNetNum = familyNetNum;
	}

	public List<FamilyMember> getFamilyMembers() {
		return familyMembers;
	}

	public void setFamilyMembers(List<FamilyMember> familyMembers) {
		this.familyMembers = familyMembers;
	}
    
    
}
