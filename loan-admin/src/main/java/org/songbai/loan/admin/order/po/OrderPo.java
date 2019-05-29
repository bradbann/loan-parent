package org.songbai.loan.admin.order.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class OrderPo extends PageRow {

    String orderNumber;
    String userPhone;//用户手机号
    String startDate;//下单开始时间
    String endDate;//下单结束时间
    Integer agencyId;
    Integer orderStage; //订单阶段
    Integer orderStatus;//状态
    Integer orderAuthStatus;//后台辅助状态(仅取单/退单)0-否，1-是
    Integer actorId;//取单人id

    String paymentTimeStart;//应还日期开始
    String paymentTimeEnd;//应还日期开始
    String realPaymentTimeStart;//实际还款日期开始
    String realPaymentTimeEnd;//实际还款日期开始

    String status;//状态
//    Integer channelId;//渠道id
    Integer isDeduct;//是否已代扣,0-否，1-是
    Integer guest;
    Integer vestId;//马甲id
    String channelCode;//渠道code

}
