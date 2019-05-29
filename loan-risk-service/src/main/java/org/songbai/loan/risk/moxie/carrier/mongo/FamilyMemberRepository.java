package org.songbai.loan.risk.moxie.carrier.mongo;

import org.songbai.loan.risk.moxie.carrier.model.FamilyMemberModel;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 运营商套餐适用情况
 * ClassName: PackageUsageRepository
 * date: 2016年7月19日 下午6:29:23
 */
@Repository
public interface FamilyMemberRepository extends MongoRepository<FamilyMemberModel, String> {


//    @Delete("delete from risk_mx_mb_familynet where user_id=#{userId} and mobile=#{mobile} and family_net_num=#{familyNetNum}")
//    public void deleteFamilyMember(@Param("userId") String userId, @Param("mobile") String mobile, @Param("familyNetNum") String familyNetNum);

    @DeleteQuery("{ \"userId\":?0,\"mobile\":?1,\"familyNetNum\":?2  }")
    public void deleteFamilyMember(String userId, String mobile, String familyNetNum);


    @Query("{ \"userId\":?0,\"mobile\":?1}")

    public List<FamilyMemberModel> selectFamilyMemberModel(String userId, String mobile);
}
