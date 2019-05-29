package org.songbai.loan.user.finance.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.songbai.loan.model.loan.OrderModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayOrderVO {

    private OrderModel orderModel;

    private Integer agencyId;
    private Integer userId;


    private String orderNumber;
    private Integer orderId;

    private Double payment;//应付金额

    private Integer payRate ; // 用户支付额度; 用于自动扣款




}
