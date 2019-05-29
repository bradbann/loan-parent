package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuandong on 17/8/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaobaoData {
    private TaobaoUserInfo userinfo;
    private List<DeliverAddress> deliveraddress;
    private List<RecentDeliverAddress> recentdeliveraddress;

    private List<TradeDetails> tradedetails = new ArrayList<TradeDetails>();

    @JsonProperty("alipaywealth")
    private TaobaoAlipayWealth taobaoAlipayWealth;

    public TaobaoAlipayWealth getTaobaoAlipayWealth() {
        return taobaoAlipayWealth;
    }

    public void setTaobaoAlipayWealth(TaobaoAlipayWealth taobaoAlipayWealth) {
        this.taobaoAlipayWealth = taobaoAlipayWealth;
    }

    public TaobaoUserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(TaobaoUserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public List<DeliverAddress> getDeliveraddress() {
        return deliveraddress;
    }

    public void setDeliveraddress(List<DeliverAddress> deliveraddress) {
        this.deliveraddress = deliveraddress;
    }

    public List<RecentDeliverAddress> getRecentdeliveraddress() {
        return recentdeliveraddress;
    }

    public void setRecentdeliveraddress(List<RecentDeliverAddress> recentdeliveraddress) {
        this.recentdeliveraddress = recentdeliveraddress;
    }

    public List<TradeDetails> getTradedetails() {
        return tradedetails;
    }

    public void setTradedetails(List<TradeDetails> tradedetails) {
        this.tradedetails = tradedetails;
    }
}
