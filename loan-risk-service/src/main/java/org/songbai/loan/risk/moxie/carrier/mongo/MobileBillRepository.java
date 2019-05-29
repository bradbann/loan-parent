package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MobileBillModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 这里用的是jdbctemplate，DAO层可以根据自己的业务来选择
 * ClassName: MobileBillRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileBillRepository extends MongoRepository<MobileBillModel,String> {


//    @Select("select * from risk_mx_mb_mobilebill where user_id=#{userId} and mobile=#{mobile} and bill_month=#{billMonth}")
//    public MobileBillModel getMobileBill(@Param("userId") String userId, @Param("mobile") String mobile, @Param("billMonth") String billMonth);

    @Query("{ \"userId\":?0,\"mobile\":?1,\"billMonth\":?2  }")
    public MobileBillModel getMobileBill(String userId, String mobile, String billMonth);


}
