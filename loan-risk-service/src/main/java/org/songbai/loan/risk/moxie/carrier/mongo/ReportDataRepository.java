package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.ReportDataModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface ReportDataRepository extends MongoRepository<ReportDataModel, String> {
//    @Delete("delete from risk_mx_mb_reportdata where user_id=#{userId} and mobile=#{mobile}")
//    public void deleteReportData(String userId, String mobile);
//
//    @Select("select * from risk_mx_mb_reportdata where user_id=#{userId} and mobile=#{mobile} ")
//    public ReportDataModel getReportData(@Param("userId") String userId, @Param("mobile") String mobile);


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1 }")
    public void deleteReportData(String userId, String mobile);

    @Query("{ \"userId\":?0,\"mobile\":?1 }")
    public ReportDataModel getReportData(String userId, String mobile);

    public ReportDataModel getReportDataModelByUserIdAndTaskId(String userId, String taskId);


}
