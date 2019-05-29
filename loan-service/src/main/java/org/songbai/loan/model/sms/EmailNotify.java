package org.songbai.loan.model.sms;

import lombok.Data;

import java.util.Map;

@Data
public class EmailNotify {

    private String agencyCode;
    private Integer emailType;

    private String email;

    private String teleCode;

    private Map<String, Object> param;
    private long createTime;
}
