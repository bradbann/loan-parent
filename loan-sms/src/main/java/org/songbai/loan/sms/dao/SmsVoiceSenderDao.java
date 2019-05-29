package org.songbai.loan.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.sms.SmsVoiceModel;

public interface SmsVoiceSenderDao extends BaseMapper<SmsVoiceModel> {


    SmsVoiceModel findAgencySenderVoice(Integer agencyId);

}
