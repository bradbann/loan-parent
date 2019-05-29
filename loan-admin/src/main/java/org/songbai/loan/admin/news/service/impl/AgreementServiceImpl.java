package org.songbai.loan.admin.news.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.mongo.AgreementDao;
import org.songbai.loan.admin.news.service.AgreementService;
import org.songbai.loan.model.news.AgreementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgreementServiceImpl implements AgreementService {
    private Logger logger = LoggerFactory.getLogger(AgreementService.class);

    @Autowired
    private AgreementDao agreementDao;


    @Override
    public Page<AgreementModel> findAgreementByPage(AgreementModel agreementModel, Integer page, Integer pageSize, Integer AgencyId) {
        if (AgencyId != 0) { //如果当前用户不是平台用户的时候，默认只能查询自己的数据
            agreementModel.setAgencyId(AgencyId);
        }

        Integer index = page * pageSize;

        return agreementDao.findAgreementByPage(agreementModel, index, pageSize);

    }

    @Override
    public AgreementModel findAgreementById(String id) {

        return agreementDao.findAgreementById(id);
    }

    @Override
    public void saveAgreement(AgreementModel agreementModel) {

//        AgreementModel oldAgree = agreementDao.findAgreementByCode(agreementModel.getCode(), agreementModel.getAgencyId());
//
//        if (oldAgree != null) {
//            throw new ResolveMsgException("common.param.repeat", "code");
//        }

        agreementDao.insertAgreement(agreementModel);
    }

    @Override
    public void updateAgreement(AgreementModel agreementModel) {

        AgreementModel oldAgree = agreementDao.findAgreementById(agreementModel.getId());

        if (oldAgree == null) {
            throw new ResolveMsgException("common.param.repeat", "model");
        }

        agreementDao.updateAgreement(agreementModel);
    }

    @Override
    public void deleteAgreement(String ids, Integer agencyId) {
        agreementDao.deleteAgreementById(agencyId, ids);
    }

}
