package org.songbai.loan.service.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.sms.SmsVoiceModel;

public interface ComSmsVoiceSenderDao extends BaseMapper<SmsVoiceModel> {


    SmsVoiceModel findAgencySenderVoice(Integer agencyId);

}
