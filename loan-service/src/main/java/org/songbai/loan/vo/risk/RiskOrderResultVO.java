package org.songbai.loan.vo.risk;


import lombok.Data;

@Data
public class RiskOrderResultVO {


    /**
     * DEFAULT(0, "默认"), PASS(1, "通过"), REJECT(2, "拒绝"), MAN(3, "人工");
     */
    private Integer result; // 结果，0，待复审， 1,自动通过 ，2 自动拒绝
    private String resultMsg; //拒绝原因

    private String userId;
    private String orderNumber;//订单号


}
