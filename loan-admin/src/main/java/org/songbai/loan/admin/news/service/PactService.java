package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.PactVo;
import org.songbai.loan.model.news.PactModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PactService {
    void addPact(PactModel pactModel);

    void updatePact(PactModel model);

    void deletePact(String ids, Integer dataId);

    PactModel findPactById(String id);

    Page<PactVo> findPactPage(PactModel param, Integer page, Integer pageSize);

    List<PactModel> findPactList(PactModel param);
}
