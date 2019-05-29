package org.songbai.loan.model.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/9 5:04 PM
 */
@Document(collection = "loan_u_device")
@Data
public class UserDeviceModel {
    private String id;
    private Integer userId;
    private String device;

    private Date createTime;
    private Date updateTime;
}
