package org.songbai.loan.sms.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "user_sms_log")
public class SmsLog implements Serializable {

    @Id
    private String id;

    @Indexed
    private Integer agencyId;

    @Indexed
    private Integer vestId;

    @Indexed
    private String phone;

    @Indexed
    private String mid;// 短信方返回的消息id

    private String msg;//短信发送内容

    /**
     * @see org.songbai.loan.constant.sms.SmsConstant.SenderType
     */
    private Integer senderType;// duanx


    private JSONObject data;

    private Date createTime;


}
