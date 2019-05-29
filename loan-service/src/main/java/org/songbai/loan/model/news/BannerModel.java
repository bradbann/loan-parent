package org.songbai.loan.model.news;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by xuesong on 14:45 2018/02/08
 */
@Data
@Document(collection = "news_banner")
public class BannerModel {

    private String id;
    private Integer clicks; // 点击次数
    private Integer agencyId;//代理关联id
    private String agencyName;//代理名称
    private String title; //标题
    private String subTitle; // 副标题
    private String content; // 内容

    private Integer jumpType; // 跳转至
    private String jumpContent; // 跳转内容
    private Integer status; // 状态（可见不可见）0 不显示  1显示
    private Integer index; // 排序
    private Integer showcase; //  banner展示位  暂时只有首页  0  首页
    private String excludeVersion; // 不显示的版本号
    private String includeVersion; // 只显示的版本号
    /**
     * 适用范围：1安卓,2ios,3web,4客户端,
     */
    private List<Integer> scopes;
    private List<Integer> vestlist; // 马甲包Id

    private Date showStartTime; // 开始显示的时间
    private Date showEndTime; // 结束显示的时间

    private String operator;// 操作人
    private Date createTime;
    private Date updateTime;
//    private List<Integer> includeAgency;//显示范围，0-平台，1-代理
}
