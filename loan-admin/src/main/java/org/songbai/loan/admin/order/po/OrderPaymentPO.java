package org.songbai.loan.admin.order.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class OrderPaymentPO {
    private Integer agencyId;
    private Integer status;//1待放款 6 放款失败
    private String orderNumber;
    private String startDate;//开始时间
    private String endDate;//结束时间
    private String userPhone;//用户手机号
    private Integer reviewId;//复审人

    private Integer vestId;//马甲id
    private String channelCode;//渠道code
}
