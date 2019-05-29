package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MobileSmsModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileSmsRepository extends MongoRepository<MobileSmsModel,String> {

//    @Insert("<script> insert into risk_mx_mb_mobilesms (user_id,mobile,bill_month,time,peer_number,location,send_type,msg_type,service_name,fee) values" +
//            "<foreach collection=\"smsList\" item=\"item\" separator=\",\">" +
//            "(#{userId},#{item.mobile},#{item.billMonth},#{item.time},#{item.peerNumber},#{item.location},#{item.sendType},#{item.msgType},#{item.serviceName},#{item.fee})" +
//            "</foreach> " +
//            "</script>")
//
//    public int batchSave(@Param("userId") String userId, @Param("smsList") List<MobileSmsModel> smsList);

//    @Delete("delete from risk_mx_mb_mobilesms where user_id= #{0} and mobile = #{1} and bill_month = #{2}")
//    public void deleteMobileSms(String userId, String mobile, String billMonth);


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1,\"billMonth\":?2  }")
    public void deleteMobileSms(String userId, String mobile, String billMonth);

}
