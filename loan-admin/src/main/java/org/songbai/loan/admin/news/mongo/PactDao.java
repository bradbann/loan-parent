package org.songbai.loan.admin.news.mongo;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.PactVo;
import org.songbai.loan.model.news.PactModel;

import java.util.List;

public interface PactDao {
    PactModel findPactByCode(String code, Integer agencyId);

    void addPact(PactModel pactModel);

    PactModel findPactById(String id);

    void updatePact(PactModel model);

    void deletePactById(Integer agencyId, String... ids);

    Page<PactVo> findPactPage(PactModel param, Integer index, Integer pageSize);

    List<PactModel> findPactList(PactModel param);
}
