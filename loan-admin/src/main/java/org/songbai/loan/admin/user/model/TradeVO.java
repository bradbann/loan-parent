package org.songbai.loan.admin.user.model;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/12/18 7:52 PM
 */
@Data
public class TradeVO {
    private String cnameLevel1;//交易类型
    private String sellerShopName;//交易对方
    private String tradeId;//交易id
    private String itemName;//商品名称
    private String tradeText;//交易状态
    private Integer actualFee;//交易金额(分)
    private String tradeFee;//交易金额(元)
    private Date tradeCreateTime;//交易时间
}
