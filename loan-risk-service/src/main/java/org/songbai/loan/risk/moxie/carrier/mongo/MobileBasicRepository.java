package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MobileBasicModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 这里用的是jdbctemplate，DAO层可以根据自己的业务来选择
 * ClassName: MobileBasicRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileBasicRepository extends MongoRepository<MobileBasicModel,String> {

//
//    @Select("select * from risk_mx_mb_mobilebasic where userId=#{userId} and mobile=#{mobile}")
//    MobileBasicModel getMobileBasic(@Param("userId") String userId, @Param("mobile") String mobile);

    @Query("{ \"userId\":?0,\"mobile\":?1 }")
    MobileBasicModel getMobileBasic(String userId, String mobile);


}
