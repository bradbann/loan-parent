package org.songbai.loan.risk.moxie.magic.mongo;

import org.songbai.loan.risk.moxie.magic.model.MagicReportModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MagicReportRepository extends MongoRepository<MagicReportModel, String> {


    /**
     *  以前的userId 为 用户的thirdId ,  新版的userId 为 idcard:511322199001010291
     *  目的是用户的报告
     * @param userId
     * @return
     */
    MagicReportModel getMagicReportModelByUserId(String userId);

    /**
     *
     * @param idcard
     * @return
     */
    MagicReportModel getMagicReportModelByIdcard(String idcard);


}
