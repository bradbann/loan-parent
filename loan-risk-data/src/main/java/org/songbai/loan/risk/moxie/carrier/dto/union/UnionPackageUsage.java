package org.songbai.loan.risk.moxie.carrier.dto.union;

import org.songbai.loan.risk.moxie.carrier.dto.PackageItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown=true)
public class UnionPackageUsage{
    @JsonProperty("bill_start_date")
    private String billStartDate;

    @JsonProperty("bill_end_date")
    private String billEndDate;

    private List<PackageItem> items = new ArrayList<>();

	public String getBillStartDate() {
		return billStartDate;
	}

	public void setBillStartDate(String billStartDate) {
		this.billStartDate = billStartDate;
	}

	public String getBillEndDate() {
		return billEndDate;
	}

	public void setBillEndDate(String billEndDate) {
		this.billEndDate = billEndDate;
	}

	public List<PackageItem> getItems() {
		return items;
	}

	public void setItems(List<PackageItem> items) {
		this.items = items;
	}


}
