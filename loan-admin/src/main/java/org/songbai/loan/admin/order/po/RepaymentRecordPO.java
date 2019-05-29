package org.songbai.loan.admin.order.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class RepaymentRecordPO {
    private Integer agencyId;
    private Integer type;// 还款类型 1线上 2线下
    private String repayType;//还款方式 支付宝,微信等
    private Integer repayStatus;//还款状态 2正常还款,5逾期还款 6提前还款, 7催收还款

    private String userPhone;//用户手机号
    private String repaymentNumber;//还款单号
    private String orderNumber;//订单号

    private String startDate;//开始时间
    private String endDate;//结束时间

    private Integer vestId;//马甲id
    private String channelCode;//渠道code
}
