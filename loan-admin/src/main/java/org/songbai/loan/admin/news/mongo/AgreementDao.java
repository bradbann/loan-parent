package org.songbai.loan.admin.news.mongo;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.model.news.AgreementModel;

import java.util.List;

public interface AgreementDao {
    Page<AgreementModel> findAgreementByPage(AgreementModel agreementModel, Integer index, Integer size);

    AgreementModel findAgreementById(String id);

    AgreementModel findAgreementByCode(String code, Integer agencyId);

    void updateAgreement(AgreementModel agreementModel);

    void deleteAgreementById(Integer agencyId, String... ids);

    void insertAgreement(AgreementModel agreementModel);

}
