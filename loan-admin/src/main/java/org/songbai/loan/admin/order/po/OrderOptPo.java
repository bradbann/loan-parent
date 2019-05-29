package org.songbai.loan.admin.order.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class OrderOptPo extends PageRow {
    Integer stage;//阶段
    Integer status;//状态,0-失败，1-成功、
    String reviewName;//审核人
    String reviewId;//审核人id
    String userPhone;//用户手机号
    String startDate;//开始时间
    String endDate;//结束时间
    Integer agencyId;
    String userId;


}
