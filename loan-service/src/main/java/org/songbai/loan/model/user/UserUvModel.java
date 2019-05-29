package org.songbai.loan.model.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "user_uv")
public class UserUvModel {

    private String id;
    private String ip; //ip地址
    private String channelCode;
    private Integer channelId; // 渠道id
    private String ua; //文档类型 h5
    private Integer agencyId;
    private Integer vestId;
    private Date createTime;
    private Date modifyTime;
}
