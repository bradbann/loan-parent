package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class OrderRepayRecordVO {
    private String orderNumber;
    private String repaymentNumber;//订单号

    private String username;//用户昵称
    private String phone;//手机号
    private Integer type;
    private String repayType;//还款类型 支付宝,微信等
    private Integer repayStatus;//还款状态 2正常还款,5逾期还款 6提前还款, 7催收还款

    private String money;//支付金额
    private String payment;//应还金额
    private String bankName;// 银卡昵称
    private String bankNumber;// 卡号
    private String autoRepayment;//代扣金额

    private String receipt;// 凭证

    private String remark;// 备注


    private Integer actorId;//放款人id
    private String optName;//操作人昵称

    private Date repaymentTime;// 还款时间
    private Date paymentTime;// 放款时间

    private Date createTime;//下单时间
    private Integer agencyId;
    private String agencyName;

    String channelCode;
    String vestName;
    Integer vestId;
}
