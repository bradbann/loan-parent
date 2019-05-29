package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderMachineVo {
    String orderNumber;
    String userName;
    String userPhone;
    Integer scoring; //风控评分
    Date orderTime;
    String riskResultMsg;//风控拒绝原因
    Integer agencyId;
    String agencyName;
}
