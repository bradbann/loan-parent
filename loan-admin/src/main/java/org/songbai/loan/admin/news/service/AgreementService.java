package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.model.news.AgreementModel;

public interface AgreementService {
    Page<AgreementModel> findAgreementByPage(AgreementModel agreementModel, Integer page, Integer pageSize, Integer agencyId);

    AgreementModel findAgreementById(String id);

    void saveAgreement(AgreementModel agreementModel);

    void updateAgreement(AgreementModel agreementModel);

    void deleteAgreement(String ids, Integer agencyId);

}
