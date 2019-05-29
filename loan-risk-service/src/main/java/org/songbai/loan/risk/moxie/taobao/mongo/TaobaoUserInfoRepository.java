package org.songbai.loan.risk.moxie.taobao.mongo;

import org.songbai.loan.risk.moxie.taobao.model.UserInfoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TaobaoUserInfoRepository extends MongoRepository<UserInfoModel,String> {

    @Query("{ \"userId\":?0, \"mappingId\": ?1 } ")
    UserInfoModel getUserInfo(String userId, String mappingId);
}
