package org.songbai.loan.sms.mongo;

import org.songbai.loan.sms.model.SmsLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: qmw
 * Date: 2019/1/21 3:33 PM
 */
@Repository
public interface SmsLogRepository extends MongoRepository<SmsLog, String> {

    List<SmsLog> findByAgencyIdAndSenderType(Integer agencyId, Integer senderType);

}
