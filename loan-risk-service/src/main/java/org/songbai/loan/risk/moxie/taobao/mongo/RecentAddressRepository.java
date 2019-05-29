package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.RecentAddressModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface RecentAddressRepository extends MongoRepository<RecentAddressModel, String> {


//    @Select("select * from risk_mx_tb_recent_address where user_id = #{userId} and mapping_id = #{mappingId} and trade_id = #{tradeId}")
//    RecentAddressModel getRecentAddress(@Param("userId") String userId,@Param("mappingId") String mappingId,@Param("tradeId") String tradeId);


    @Query("{ \"userId\":?0, \"mappingId\": ?1,\"tradeId\":?2 } ")
    RecentAddressModel getRecentAddress(String userId, String mappingId, String tradeId);
}
  
