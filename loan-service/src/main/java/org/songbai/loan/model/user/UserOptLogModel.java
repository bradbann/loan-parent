package org.songbai.loan.model.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "ex_user_opt_log")
public class UserOptLogModel {

    private String id;
    private Integer userId; // 用户id
    private String userName;  // 用户姓名
    private String ip;          // 用户ip

    private String ipAddr;     // ip 对应的地址  Country + city + region ;
    private String browserAgent; //浏览器
    private String device;    // 设备

    private Integer optType;    //操作类型

    private String beforeValue;// 修改之前的值
    private String afterValue; // 修改之后的值


    private Date createTime;
}
