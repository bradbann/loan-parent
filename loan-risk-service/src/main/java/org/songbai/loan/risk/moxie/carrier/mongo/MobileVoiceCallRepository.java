package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MobileVoiceCallModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileVoiceCallRepository extends MongoRepository<MobileVoiceCallModel,String> {

//    @Insert("<script> insert into risk_mx_mb_voicecall (user_id,mobile,bill_month,time,peer_number,location,location_type,duration_in_second,dial_type,fee) values" +
//            "<foreach collection=\"calls\" item=\"item\" separator=\",\">" +
//            "(#{userId},#{item.mobile},#{item.billMonth},#{item.time},#{item.peerNumber},#{item.location},#{item.locationType},#{item.durationInSecond},#{item.dialType},#{item.fee} )" +
//            "</foreach> " +
//            "</script>")
//    public int batchSave(@Param("userId") String userId, @Param("calls") List<MobileVoiceCallModel> calls);


//    @Delete("delete from risk_mx_mb_voicecall where user_id=#{userId} and mobile=#{mobile} and bill_month = #{billMonth}")
//    public void deleteMobileVoiceCall(@Param("userId") String userId, @Param("mobile") String mobile, @Param("billMonth") String billMonth);



    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1,\"billMonth\":?2  }")
    public void deleteMobileVoiceCall(String userId, String mobile, String billMonth);

}
