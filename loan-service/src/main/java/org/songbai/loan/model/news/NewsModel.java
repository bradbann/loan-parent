package org.songbai.loan.model.news;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "news_information")
public class NewsModel {
    private String id;
    private String title; // 资讯标题
    private String summary; // 资讯摘要
    private Integer format; // 资讯内容格式
    private String content; // 资讯内容
    private String cover; // 资讯封面
    private Integer type; // 资讯类型 // 1，公告， 2 ，新闻
    private String style; // 资讯风格
    private Integer status; // 状态
    private Integer index; // 排序标识
    private String source; // 资讯来源

    private Date showStartTime;
    private Date showEndTime;

    private String operator; // 操作员

    private Date createTime;
    private Date updateTime;

    private Integer agencyId;//代理关联id
//    private List<Integer> includeAgency;//显示范围，0-平台，1-代理

    private List<Integer> vestlist; // 马甲包Id

    /**
     * 适用范围：1安卓,2ios,3web,4客户端
     */
    private List<Integer> scopes;

}
