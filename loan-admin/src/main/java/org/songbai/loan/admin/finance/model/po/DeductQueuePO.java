package org.songbai.loan.admin.finance.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class DeductQueuePO extends PageRow {
    String orderNumber;
    String userPhone;
    String startRepayMentDate;
    String endRepayMentDate;
    Integer vestId;
    Integer agencyId;
    Integer status;
    //Integer deductStatus;
}
