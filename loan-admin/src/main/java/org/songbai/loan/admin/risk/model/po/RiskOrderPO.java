package org.songbai.loan.admin.risk.model.po;


import lombok.Data;

@Data
public class RiskOrderPO {

    private String userId;
    private String orderNumber;
    private Integer mouldId;
    private Integer status; // 0 提交订单 ，1 机审完成，2，等待数据


    private Integer page;
    private Integer pageSize;

}
