package org.songbai.loan.risk.moxie.carrier.billitem;

import lombok.Data;

/**
 * Created by zengdongping on 17/1/3.
 */
@Data
public class CarrierReportTask {
    private String mobile;
    private String userId;
    private String name;
    private String idcard;
    private String taskId;
    private String contact;
    private boolean result;
    private String message;

}
