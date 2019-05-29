package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.TradeDetailModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface TaobaoTradeInfoRepository extends MongoRepository<TradeDetailModel,String> {


//    @Select("select * from risk_mx_tb_trade_detail where user_id = #{0} and mapping_id = #{1} and trade_id = #{2}")
//    TradeDetailModel getTradeDetailEntity(String userId, String mappingId, String tradeId);


    @Query("{ \"userId\":?0, \"mappingId\": ?1,\"tradeId\":?2 } ")
    TradeDetailModel getTradeDetailEntity(String userId, String mappingId, String tradeId);


}
