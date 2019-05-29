package org.songbai.loan.model.sms;

import lombok.Data;

import java.util.Map;

@Data
public class SmsNotify {

    private String ip;

    private String phone;
    private Integer agencyId;
    private Integer vestId;

    private String voiceCode;

    private Map<String, Object> param;

    /**
     * @see {SmsConst}
     */
    private Integer smsType; // 短信类型。

    private long createTime;

}
