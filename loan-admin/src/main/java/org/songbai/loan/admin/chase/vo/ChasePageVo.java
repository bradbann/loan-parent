package org.songbai.loan.admin.chase.vo;

import lombok.Data;
import org.songbai.loan.model.loan.OrderModel;

@Data
public class ChasePageVo extends OrderModel {
    String userName;
    String userPhone;

}
