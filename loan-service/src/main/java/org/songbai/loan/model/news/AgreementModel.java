package org.songbai.loan.model.news;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Data
@Document(collection = "news_agreement")
public class AgreementModel {

    private String id;
    private String code; //类别编码

    private String title; // 文章标题
    private Integer format; //文档类型 h5
    private String content; // 内容 ， 如果是 h5 表示h5 地址， 如果是html ，表示html 内容

    private String operator; // 操作员
    private Date createTime;
    private Date modifyTime;
    private Integer agencyId;//代理关联id
//    private List<Integer> includeAgency;//显示范围，0-平台，1-代理

    //----------新增字段
    private String author; //作者
    private String summary; //摘要



}
