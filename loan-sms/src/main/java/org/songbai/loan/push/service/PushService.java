package org.songbai.loan.push.service;

import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.sms.PushSenderModel;

/**
 * Author: qmw
 * Date: 2018/11/14 11:25 AM
 */
public interface PushService {
    void push(PushModel message, PushSenderModel sender);
}
