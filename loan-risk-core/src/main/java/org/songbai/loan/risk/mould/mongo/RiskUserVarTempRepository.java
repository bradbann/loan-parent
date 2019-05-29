package org.songbai.loan.risk.mould.mongo;

import org.songbai.loan.risk.model.user.RiskUserVarTempModel;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskUserVarTempRepository extends MongoRepository<RiskUserVarTempModel, String> {

    @CountQuery("{ \"userId\":?0,\"catalog\":?1,\"sources\":?2  }")
    public Integer countVarTempModel(String userId, Integer catalog, String sources);


    @CountQuery("{ \"userId\":?0,\"catalog\":?1 }")
    public List<RiskUserVarTempModel> selectVarTempModel(String userId, Integer catalog);



    @DeleteQuery("{ \"userId\":?0,\"catalog\":?1 }")
    public void deleteVarTempModel(String userId, Integer catalog);


}
