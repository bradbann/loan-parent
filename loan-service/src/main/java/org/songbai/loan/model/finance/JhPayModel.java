package org.songbai.loan.model.finance;

import lombok.Data;

@Data
public class JhPayModel {
    String merchantOutOrderNo;//商户订单号
    String merid;// 商户号
    String orderMoney;//订单金额
    String noncestr;//随机参数
    String orderNo;//一麻袋平台支付订单
    Integer payResult;//订单返回结果,1 为成功,0为失败，3为处理中
    String payTime;//yyyyMMddHHmmss（完成支付的时间。若payResult值为0或3，则该值为null。）
    Integer code;//错误代码
    String msg;//错误信息
    String sign;
    String id;//order_number
    String aliNo;//阿里流水号
}
