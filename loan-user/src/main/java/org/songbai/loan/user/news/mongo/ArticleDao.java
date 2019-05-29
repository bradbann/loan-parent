/**
 * @author huanglei
 * @date 2017年4月16日
 */

package org.songbai.loan.user.news.mongo;


import org.songbai.loan.model.news.AgreementModel;
import org.songbai.loan.model.news.ArticleModel;

import java.util.List;

public interface ArticleDao {


	public ArticleModel findArticleById(String id);

	List<ArticleModel> findArticleListByCode(String code, String lang, Long endTime, Integer size);

	List<ArticleModel> findSimpleArticleListByCode(String code, Long endTime, Integer size, Integer agencyId);

	boolean containArticleLang(String code, String lang);

	List<ArticleModel> findArticleByCode(String code);

	AgreementModel findAgreementModel(String code, Integer agencyId);

}