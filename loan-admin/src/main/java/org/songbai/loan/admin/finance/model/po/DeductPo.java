package org.songbai.loan.admin.finance.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class DeductPo extends PageRow {
    Integer agencyId;
    Integer vestId;
    String orderNumber;
    String userPhone;
    String startRepayMentDate;
    String endRepayMentDate;
    String startDeductDate;
    String endDeductDate;
    String payPlatform;
    Integer deductStatus;
}
