package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 8:46 PM
 */
@Data
public class OrderPayRecordVO {
    private String orderNumber;
    private String paymentNumber;//订单号
    //private Integer actorId;//放款人id

    private String username;//用户昵称
    private String phone;//手机号

    private String money;//支付金额

    private String autoRepayment;//代扣金额
    private String payChannel;//支付渠道昵称

    private String bankName;// 银卡昵称
    private String branchBank;// 支行名称
    private String bankNumber;// 卡号

    private String remark;// 备注

    private Date paymentTime;// 支付时间
    private Date orderTime;// 支付时间
    private Date createTime;//下单时间
    Integer agencyId;
    String agencyName;
    String channelCode;
    String vestName;
    Integer vestId;
}
