package org.songbai.loan.admin.news.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.PactVo;
import org.songbai.loan.admin.news.mongo.PactDao;
import org.songbai.loan.admin.news.service.PactService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.news.PactModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PactServiceImpl implements PactService {
    @Autowired
    PactDao pactDao;

    @Override
    public void addPact(PactModel pactModel) {
        PactModel oldModel = pactDao.findPactByCode(pactModel.getCode(), pactModel.getAgencyId());
        if (oldModel != null) {
            throw new ResolveMsgException("common.param.repeat", "code");
        }
        pactDao.addPact(pactModel);
    }

    @Override
    public void updatePact(PactModel model) {
        PactModel oldModel = pactDao.findPactById(model.getId());
        if (oldModel == null) {
            throw new BusinessException(AdminRespCode.PACT_NOT_EXISIT, "协议不存在");
        }
        PactModel old = pactDao.findPactByCode(model.getCode(), oldModel.getAgencyId());
        if (old != null && !model.getId().equals(old.getId())) {
            throw new ResolveMsgException("common.param.repeat", "code");
        }

        pactDao.updatePact(model);
    }

    @Override
    public void deletePact(String ids, Integer agencyId) {
        pactDao.deletePactById(agencyId, ids);
    }

    @Override
    public PactModel findPactById(String id) {
        return pactDao.findPactById(id);
    }

    @Override
    public Page<PactVo> findPactPage(PactModel param, Integer page, Integer pageSize) {
        Integer index = page * pageSize;
        return pactDao.findPactPage(param, index, pageSize);
    }

    @Override
    public List<PactModel> findPactList(PactModel param) {
        return pactDao.findPactList(param);
    }
}
