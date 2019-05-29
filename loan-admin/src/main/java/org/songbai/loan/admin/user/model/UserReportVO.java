package org.songbai.loan.admin.user.model;


import lombok.Data;

@Data
public class UserReportVO {


    private String taobaoReportMessage;
    private String taobaoReportData;

    private String carrierReportMessage;
    private String carrierReportData;

    private String moxieReportData;
}
