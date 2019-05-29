package org.songbai.loan.admin.order.vo;

import lombok.Data;

@Data
public class OrderOptPageVo {
    String userName;
    String userPhone;
    String orderStatusName;
    String reviewer;//审核人
    String reviewTime;
    String remark;//备注
    Integer id;
    Integer status;//审核状态,0-失败，1-成功
    String orderNumber;
    Integer agencyId;
    String agencyName;
    Integer userId;
    String thirdId;

}
