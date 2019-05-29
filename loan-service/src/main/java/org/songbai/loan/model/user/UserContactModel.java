package org.songbai.loan.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户通讯录
 */
@Data
@Document(collection = "loan_user_contacts")
public class UserContactModel {
    @Id
    private String id;

    @Indexed
    private Integer userId;

    private String name;//联系人名称
    private String phone;//联系人手机号

    private String email;
    private String relation;// 跟用户的关系
    private String doubtfulRelation; // 疑似关系， 一般又程序自动推断
    private String attachAddress; // 归属地

    private Date createTime;
    private Date updateTime;
}
