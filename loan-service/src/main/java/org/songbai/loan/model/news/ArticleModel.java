/**
 * @author huanglei
 * @date 2017年4月17日
 */

package org.songbai.loan.model.news;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Document(collection = "news_article")
public class ArticleModel {

    private String id;

    private String categoryCode; //类别编码
    private Integer categoryType; //类别类型

    private String title; // 文章标题
    private String subTitle; // 文章副标题
    private List<String> imgs = new ArrayList<>();// 封面图
    private String publishTime; // 发布时间
    private String publishUser; // 发布用户
    /**
     * @see {ArticleConst.FORMAT_*}
     */
    private Integer format; //文档类型 h5
    private String content; // 内容 ， 如果是 h5 表示h5 地址， 如果是html ，表示html 内容
    private Integer clicks; // 文章的点击数量
    private String remark; // 文章备注
    private List<String> labels; // 文章标签
    private String sources; // 来源
    private Integer index ; //排序


    private String operator; // 操作员
    private Date createTime;
    private Date modifyTime;
    private Integer agencyId;//代理关联id
//    private List<Integer> includeAgency;//显示范围，0-平台，1-代理
}