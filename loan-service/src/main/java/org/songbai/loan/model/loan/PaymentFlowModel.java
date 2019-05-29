package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 8:09 PM
 */
@Data
@TableName("loan_a_payment_flow")
public class PaymentFlowModel {
    private Integer id;
    private String orderNumber;//订单号
    private String paymentNumber;//放款单号
    private Integer agencyId;//代理id
    private Integer userId;//用户id
    private Integer actorId;//放款人id

    private String username;//用户昵称
    private String phone;//手机号

    private Double loan;//借款金额
    private Double money;//支付金额
    private Double stampTax;//综合费
    private String payChannel;//支付渠道昵称

    private String bankName;// 银卡昵称
    private String branchBank;// 支行名称
    private String bankNumber;// 卡号

    private String remark;// 备注

    private Integer guest;//客群

    private Date paymentTime;// 支付时间
    private Date orderTime;// 支付时间
    private Date createTime;//
}
