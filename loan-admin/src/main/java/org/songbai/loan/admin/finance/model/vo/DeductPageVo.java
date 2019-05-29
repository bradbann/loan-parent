package org.songbai.loan.admin.finance.model.vo;

import lombok.Data;
import org.songbai.loan.model.loan.FinanceDeductFlowModel;

@Data
public class DeductPageVo extends FinanceDeductFlowModel {
    String vestName;
    String userName;
    String agencyName;
    Integer vestId;
    String shouldPay;//应还金额
    String platformName;//扣款渠道名称
    String userPhone;

}
