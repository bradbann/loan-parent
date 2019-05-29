package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.SubOrderModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TaobaoSuborderRepository extends MongoRepository<SubOrderModel,String> {


//    @Delete("delete from risk_mx_tb_suborder where user_id = #{userId} and mapping_id = #{mappingId} and trade_id = #{tradeId}")
//    void deleteSubOrder(@Param("userId") String userId,@Param("mappingId") String mappingId, @Param("tradeId") String tradeId);


    @DeleteQuery("{ \"userId\":?0, \"mappingId\": ?1,\"tradeId\":?2 } ")
    void deleteSubOrder(String userId, String mappingId, String tradeId);


    List<SubOrderModel> findByTradeId(String tradeId);

}
