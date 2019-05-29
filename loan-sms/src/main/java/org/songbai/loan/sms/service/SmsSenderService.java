package org.songbai.loan.sms.service;

import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsTemplate;


public interface SmsSenderService {

     void sendSms(SmsNotify notify, SmsTemplate senderTemplate,
                        SmsSender senderMessage);

}
