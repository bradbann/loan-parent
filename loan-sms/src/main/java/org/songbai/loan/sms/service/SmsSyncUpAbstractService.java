package org.songbai.loan.sms.service;

import org.songbai.loan.model.sms.SmsSender;

/**
 * Author: qmw
 * Date: 2019/1/22 1:51 PM
 */
public interface SmsSyncUpAbstractService {
    void msgSyncUp(SmsSender sender);
}
