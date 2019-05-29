package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/5 4:45 PM
 */
@Data
public class OrderPaymentVO {
    private String orderNumber;//订单号
    private String userPhone;//用户手机号
    private String username;//用户姓名
    private Integer status;//状态
    private Integer days;//借款期限
    private String loan;//借款金额
    private String obtain;//实际到账金额(应打款)
    private String remark;//备注
    private Date reviewTime;//复审时间
    private String reviewName;//操作人昵称
    private Integer reviewId;
    private Date createTime;

    String channelCode;
    String vestName;
    Integer vestId;
}
