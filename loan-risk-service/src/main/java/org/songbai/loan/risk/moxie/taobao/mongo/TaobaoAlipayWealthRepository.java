package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.TaobaoAlipayWealthModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


public interface TaobaoAlipayWealthRepository extends MongoRepository<TaobaoAlipayWealthModel,String> {


//    @Select("select * from risk_mx_tb_alipay_wealth where user_id = #{userId} and mapping_id = #{mappingId}")
//    TaobaoAlipayWealthModel getAlipayWealth(@Param("userId") String userId, @Param("mappingId") String mappingId);

    @Query("{ \"userId\":?0, \"mappingId\": ?1 } ")
    TaobaoAlipayWealthModel getAlipayWealth(String userId, String mappingId);

}
