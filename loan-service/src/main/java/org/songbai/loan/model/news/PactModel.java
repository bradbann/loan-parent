package org.songbai.loan.model.news;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "news_pact")
public class PactModel {

    private String id;
    private String code; //类别编码

    private String title; // 文章标题
    private Integer type; // 协议类型
    private String content; // 内容
    private String operator; // 操作员
    private Date createTime;
    private Date modifyTime;
    private Integer agencyId;//代理关联id

}
