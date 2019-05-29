package org.songbai.loan.risk.moxie.carrier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zengdongping on 16/10/27.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MobileRechargeItem {

    @JsonProperty("details_id")
    private String detailsId;
    @JsonProperty("recharge_time")
    private String rechargeTime;
    private Integer amount;
    private String type;

    public String getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(String detailsId) {
        this.detailsId = detailsId;
    }

    public String getRechargeTime() {
        return rechargeTime;
    }
    public void setRechargeTime(String rechargeTime) {
        this.rechargeTime = rechargeTime;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}
