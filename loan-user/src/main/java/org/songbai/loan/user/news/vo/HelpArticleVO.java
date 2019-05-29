package org.songbai.loan.user.news.vo;

import lombok.Data;
import org.songbai.loan.model.news.ArticleModel;

import java.util.List;

@Data
public class HelpArticleVO {

	private String code; // 文章编码
	private String name; // 类别名称
	private String lang; // 文档语言
	private String remark; //备注

	private Integer type;// 文章类型


	private List<ArticleModel> list;
}
