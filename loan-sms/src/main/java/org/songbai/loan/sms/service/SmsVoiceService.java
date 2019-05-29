package org.songbai.loan.sms.service;

import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsVoiceModel;

/**
 * Author: qmw
 * Date: 2019/2/15 2:39 PM
 */
public interface SmsVoiceService {
    void sendVoiceMsg(SmsVoiceModel voiceModel, SmsNotify notify);

}
