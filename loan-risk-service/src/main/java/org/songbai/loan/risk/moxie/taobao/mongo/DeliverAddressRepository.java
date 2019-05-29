package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.DeliverAddressModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface DeliverAddressRepository extends MongoRepository<DeliverAddressModel, String> {

//
//    @Select("select * from risk_mx_tb_deliver_address where user_id = #{userId} and mapping_id = #{mappingId}")
//    void deleteDeliverAddress(@Param("userId") String userId, @Param("mappingId") String mappingId);


    @DeleteQuery("{ \"userId\":?0, \"mappingId\": ?1 }")
    void deleteDeliverAddress(String userId, String mappingId);

    List<DeliverAddressModel> findByUserId(String thirdId);

}
