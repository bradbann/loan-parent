package org.songbai.loan.admin.user.mongo;

import org.songbai.loan.model.user.UserContactModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/18 7:12 PM
 */
@Repository
public interface UserConcatRepository extends MongoRepository<UserContactModel, String> {

    List<UserContactModel> findByUserId(Integer userId);
}
