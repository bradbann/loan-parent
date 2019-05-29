package org.songbai.loan.user.finance.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayResultVO {
    // 这个的支付，只是报单
    private int sts;  // 支付接口 0 失败， 1 成功 ,2 : 失败，但是可以尝试下一次扣款
    private String msg;


    private String orderNumber;
    private String payTrxId; // 支付
}
