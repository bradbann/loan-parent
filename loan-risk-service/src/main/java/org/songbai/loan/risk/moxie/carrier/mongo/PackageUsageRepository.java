package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.PackageUsageModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface PackageUsageRepository extends MongoRepository<PackageUsageModel, String> {


//	@Insert("<script> insert into risk_mx_mb_packageusage (user_id,mobile,bill_start_date,bill_end_date,item,unit,total,used) values" +
//			"<foreach collection=\"packageuseList\" item=\"item\" separator=\",\">" +
//			"(#{item.userId},#{item.mobile},#{item.billStartDate},#{item.billEndDate},#{item.item},#{item.unit},#{item.total},#{item.used})" +
//			"</foreach> " +
//			"</script>")
//    public int batchSave( @Param("packageuseList") List<PackageUsageModel> packageuseList);
//
//
//	@Delete("delete from risk_mx_mb_packageusage where user_id=#{userId} and mobile=#{mobile}")
//	public void deletePackageUsage(@Param("userId") String userId, @Param("mobile") String mobile);
//


    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1  }")
    public void deletePackageUsage(String userId, String mobile);


}
