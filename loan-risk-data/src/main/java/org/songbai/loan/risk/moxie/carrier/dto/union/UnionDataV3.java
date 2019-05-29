package org.songbai.loan.risk.moxie.carrier.dto.union;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yajun on 12/3/16.
 */
public class UnionDataV3 {

    private String mobile;

    private Integer code;

    private String message;

    private String name;

    @JsonProperty("idcard")
    private String idCard;

    private String carrier;

    private String province;

    private String city;

    @JsonProperty("open_time")
    private String openTime;

    private String level;

    @JsonProperty("package_name")
    private String packageName;

    private Integer state;

    @JsonProperty("available_balance")
    private Integer availableBalance;

    @JsonProperty("last_modify_time")
    private String lastModifyTime;

	/**
	 * 实名状态 1已实名 0未实名 -1未知
	 */
	private  int reliability;

	@JsonProperty("month_info")
	private UnionMonthInfo monthInfo;

    private List<UnionPackageUsage> packages = new ArrayList<>();

    private List<UnionFamilyNet> families = new ArrayList<>();

    private List<UnionRecharge> recharges = new ArrayList<>();

    private List<UnionBill> bills = new ArrayList<>();

    private List<UnionVoiceCall> calls = new ArrayList<>();

    private List<UnionShortMessage> smses = new ArrayList<>();

    private List<UnionNetFlow> nets = new ArrayList<>();

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
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

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Integer availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public List<UnionPackageUsage> getPackages() {
		return packages;
	}

	public void setPackages(List<UnionPackageUsage> packages) {
		this.packages = packages;
	}

	public List<UnionFamilyNet> getFamilies() {
		return families;
	}

	public void setFamilies(List<UnionFamilyNet> families) {
		this.families = families;
	}

	public List<UnionRecharge> getRecharges() {
		return recharges;
	}

	public void setRecharges(List<UnionRecharge> recharges) {
		this.recharges = recharges;
	}

	public List<UnionBill> getBills() {
		return bills;
	}

	public void setBills(List<UnionBill> bills) {
		this.bills = bills;
	}

	public List<UnionVoiceCall> getCalls() {
		return calls;
	}

	public void setCalls(List<UnionVoiceCall> calls) {
		this.calls = calls;
	}

	public List<UnionShortMessage> getSmses() {
		return smses;
	}

	public void setSmses(List<UnionShortMessage> smses) {
		this.smses = smses;
	}

	public List<UnionNetFlow> getNets() {
		return nets;
	}

	public void setNets(List<UnionNetFlow> nets) {
		this.nets = nets;
	}

	public int getReliability() {
		return reliability;
	}

	public void setReliability(int reliability) {
		this.reliability = reliability;
	}

	public UnionMonthInfo getMonthInfo() {
		return monthInfo;
	}

	public void setMonthInfo(UnionMonthInfo monthInfo) {
		this.monthInfo = monthInfo;
	}


}
