package org.songbai.loan.admin.order.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class PaymentRecordPO {
    private Integer agencyId;

    private String paymentNumber;//放款单号
    private String orderNumber;//订单号
    private String userPhone;//用户手机号

    private String startDate;//开始时间
    private String endDate;//结束时间
    private String sloanDate;//借款开始时间
    private String eloanDate;//借款结束时间

    private Integer vestId;//马甲id
    private String channelCode;//渠道code

}
