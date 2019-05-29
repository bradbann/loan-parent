package org.songbai.loan.risk.moxie.carrier.dto.union;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**

 * @email liyang@51dojo.com
 * @create 2017-10-31 下午7:53
 * @description 语音月份信息
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnionMonthInfo {

    @JsonProperty("phone_no")
    private String mobile;

    @JsonProperty("month_count")
    private int monthCount;

    @JsonProperty("miss_month_count")
    private int missMonthCount;

    @JsonProperty("no_call_month")
    private int noCallMonth;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("month_list")
    private Map<String,Integer> monthList = new HashMap<>();

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public int getMissMonthCount() {
        return missMonthCount;
    }

    public void setMissMonthCount(int missMonthCount) {
        this.missMonthCount = missMonthCount;
    }

    public int getNoCallMonth() {
        return noCallMonth;
    }

    public void setNoCallMonth(int noCallMonth) {
        this.noCallMonth = noCallMonth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Integer> getMonthList() {
        return monthList;
    }

    public void setMonthList(Map<String, Integer> monthList) {
        this.monthList = monthList;
    }
}
