package org.songbai.loan.risk.moxie.taobao.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author liyang
 * @email liyang@51dojo.com
 * @create 2017-10-24 下午3:33
 * @description 用户的淘宝对应支付宝资产
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TaobaoAlipayWealth {

    @JsonProperty("mapping_id")
    private String mappingId; // 淘宝账号在魔蝎科技中的映射ID

    @JsonProperty("balance")
    private int balance; //账户余额

    @JsonProperty("total_profit")
    private int totalProfit; //余额宝历史累计收益

    @JsonProperty("total_quotient")
    private int totalQuotient; // 余额宝金额

    @JsonProperty("huabei_creditamount")
    private int huabeiCreditamount; //花呗当前可用额度

    @JsonProperty("huabei_totalcreditamount")
    private int huabeiTotalcreditamount; // 花呗授信额度
}
