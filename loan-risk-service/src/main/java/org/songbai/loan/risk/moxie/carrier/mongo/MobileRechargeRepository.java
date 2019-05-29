package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.MobileRechargeModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface MobileRechargeRepository extends MongoRepository<MobileRechargeModel,String> {

//    @Insert("<script> insert into risk_mx_mb_recharge (user_id,mobile,recharge_time,amount,type) values" +
//            "<foreach collection=\"rechargeList\" item=\"item\" separator=\",\">" +
//            "(#{item.userId},#{item.mobile},#{item.rechargeTime},#{item.amount},#{item.type})" +
//            "</foreach> " +
//            "</script>")
//    public int batchSave(@Param("rechargeList") List<MobileRechargeModel> rechargeList);
//
//    @Delete("delete from risk_mx_mb_recharge where user_id =  #{userId} and mobile=#{mobile}")
//    public void deleteMobileRecharge(String userId, String mobile);


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1 }")
    public void deleteMobileRecharge(String userId, String mobile);


}
