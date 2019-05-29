package org.songbai.loan.admin.order.po;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/8 1:41 PM
 */
@Data
public class RepayListPO extends OptPO{
    private String userPhone;//手机号
    private String status;//状态
    private String orderNumber;//订单号
    private Integer vestId;//马甲id
    private String channelCode;//渠道code
}
