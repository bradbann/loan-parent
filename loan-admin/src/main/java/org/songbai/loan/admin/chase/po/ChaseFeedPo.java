package org.songbai.loan.admin.chase.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class ChaseFeedPo extends PageRow {
    String userPhone;
    String startChaseDate;//开始催单日期
    String endChaseDate;//结束催单日期
    String orderNumber;//订单编号
    Integer agencyId;
    String chaseId;//催收编号
    Integer deptId;
    Integer actorId;
    String userId;//第三方id
}
