package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MonthInfoModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @email liyang@51dojo.com
 * @create 2017-10-31 下午9:16
 * @description 语音月份信息crud
 **/
@Repository
public interface MonthInfoRepository extends MongoRepository<MonthInfoModel,String> {

//
//    @Delete("delete from risk_mx_mb_monthinfo where user_id=#{userId} and mobile=#{mobile}")
//    public void deleteMonthInfo(@Param("userId") String userId, @Param("mobile") String mobile);


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1 }")
    public void deleteMonthInfo(String userId, String mobile);


}
