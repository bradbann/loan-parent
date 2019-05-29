package org.songbai.loan.admin.news.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.admin.news.model.po.ArticleVo;
import org.songbai.loan.admin.news.mongo.ArticleDao;
import org.songbai.loan.admin.news.service.ArticleService;
import org.songbai.loan.model.news.ArticleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleServiceImpl implements ArticleService {
    private Logger logger = LoggerFactory.getLogger(ArticleService.class);
    // 获取src路径的正则
    private static final String IMGSRC_REG = "((http|https)://[^\":<>]*\\.(jpg|gif|bmp|bnp|png))";
    @Autowired
    private ArticleDao articleDao;

    @Override
    public void addArticle(ArticleModel article) {

        if (article.getImgs() == null || article.getImgs().size() == 0) {
            List<String> imgs = getImageSrc(article.getContent());
            //针对 包含图片的文章内容进行正则匹配图片url
            article.setImgs(imgs);
        }

        if (StringUtil.isEmpty(article.getPublishTime())) {
            article.setPublishTime(SimpleDateFormatUtil.dateToString(new Date()));
        }

        articleDao.saveArticle(article);

    }

    @Override
    public void updateArticle(ArticleModel article, Integer agencyId) {

        ArticleModel oldArtic = articleDao.queryArticleById(article.getId());

        if (oldArtic == null) {
            throw new ResolveMsgException("common.param.notnull", "oldArtic");
        }

        articleDao.updateArticleById(article);
    }


    @Override
    public ArticleModel findArticleById(String id) {
        return articleDao.queryArticleById(id);
    }

    @Override
    public ArticleModel findArticle(String categoryCode, String categoryType) {
        return articleDao.queryArticle(categoryCode, categoryType);
    }

    @Override
    public void batchDeleteArticle(String idsStr, Integer agencyId) {
        List<ArticleModel> list = articleDao.findArticleListByIds(idsStr);
        list.forEach(model -> {
            articleDao.batchDeleteArticle(Collections.singletonList(model.getId()));
        });

    }


    @Override
    public Page<ArticleVo> findArticleList(ArticleModel article, Integer page, Integer pageSize, Integer agencyId) {
        if (0 != agencyId) {//如果当前用户不是平台用户的时候，默认只能查询自己的数据
            article.setAgencyId(agencyId);
        }

        Integer index = page * pageSize;
        return articleDao.queryArticleList(article, index, pageSize);
    }


    /**
     * 正则匹配content中的图片Url集合
     */
    private static List<String> getImageSrc(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
        Pattern p = Pattern.compile(IMGSRC_REG, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        while (m.find()) {
            quote = m.group(0);
            imageSrcList.add(quote);
        }
        return imageSrcList;
    }

}
