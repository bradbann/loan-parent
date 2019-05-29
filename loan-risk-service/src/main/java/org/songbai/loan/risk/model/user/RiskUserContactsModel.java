//package org.songbai.loan.risk.model.user;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//
//@Data
//@Document(collection = "risk_user_contacts")
//public class RiskUserContactsModel {
//
//    @Id
//    private String id;
//
//    @Indexed
//    private Integer userId;
//
//    private String name;
//    private String phone;
//    private String email;
//    private String relation;// 跟用户的关系
//    private String doubtfulRelation; // 疑似关系， 一般又程序自动推断
//    private String attachAddress; // 归属地
//
//
//
//    private String data; //JSON 字段，里面的只是
//
//    private Date createTime;
//    private Date updateTime;
//
//}
