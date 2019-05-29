package org.songbai.loan.admin.chase.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class ChaseDebtPo extends PageRow {
    private String userPhone;
    private Integer startExceeDays; //开始逾期天数
    private Integer endExceeDays;   //结束逾期天数
    private Integer guest;//客群
    private String startRepaymentDate;//开始应缴日期
    String endRepaymentDate;//结束应缴日期
    String orderNumber;//订单编号
    Integer agencyId;
    Integer deptId;//催收组
    Integer chaseStatus;//是否已分配
    Integer chaseActorStatus;//是否已分配到人员
    Integer chaseActorId;//催收人
    Integer ownerChaseStatus;//订单状态,4-逾期中,3-坏账,5-已还款(对应订单状态：5，7)
    Integer vestId;//马甲名称
    Integer channelCode;//渠道code
}
