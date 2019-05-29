package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MonthItemModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @email liyang@51dojo.com
 * @create 2017-10-31 下午9:27
 * @description 话记录月份采集结果crud
 **/
@Repository
public interface MonthInfoItemRepository extends MongoRepository<MonthItemModel,String> {
//
//    @Delete("delete from risk_mx_mb_monthitem where user_id=#{userId} and mapping_id = #{mappingId}")
//    public void deleteMonthItemEntity(@Param("userId") String userId, @Param("mappingId") String mappingId);

}