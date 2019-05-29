package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.TaobaoReportModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TaobaoReportRepository extends MongoRepository<TaobaoReportModel, String> {


    @Query("{ \"userId\":?0, \"taskId\": ?1 } ")
    TaobaoReportModel getReportData(String userId, String taskId);

    @DeleteQuery("{ \"userId\":?0, \"taskId\": ?1 }")
    void deleteReportData(String userId, String taskId);

}
