package org.songbai.loan.admin.chase.vo;

import lombok.Data;
import org.songbai.loan.model.loan.OrderModel;

@Data
public class ChaseExcelVo extends OrderModel {
    String userName;
    String userPhone;
    String idcardNum;
    String productName;
    String firstContact;
    String firstPhone;
    String otherContact;
    String otherPhone;
}
