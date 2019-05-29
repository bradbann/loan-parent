package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.NetFlowModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileNetRepository extends MongoRepository<NetFlowModel,String> {


//    @Insert("<script> insert into risk_mx_mb_netflow (`user_id`,`mobile`,`bill_month`,`time`,`location`,`service_name`,`net_type`,`duration_in_second`,`duration_in_flow`,`fee`) values" +
//            "<foreach collection=\"netflows\" item=\"item\" separator=\",\">" +
//            "(#{userId},#{item.mobile},#{item.billMonth},#{item.time},#{item.location},#{item.serviceName},#{item.netType},#{item.durationInSecond},#{item.durationInFlow},#{item.fee})" +
//            "</foreach> " +
//            "</script>")
//
//    public int batchSave(@Param("userId") String userId, @Param("netflows") List<NetFlowModel> netflows);
//
//    @Delete("delete from t_netflow where user_id=#{userId} and mobile=#{mobile} and bill_month=#{billMonth}")
//    public void deleteMobileNetFlow(@Param("userId") String userId, @Param("mobile") String mobile, @Param("billMonth") String billMonth);
//


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1,\"billMonth\":?2  }")
    public void deleteMobileNetFlow(String userId, String mobile, String billMonth);


}
