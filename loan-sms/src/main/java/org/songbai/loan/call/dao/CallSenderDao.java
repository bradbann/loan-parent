package org.songbai.loan.call.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.sms.CallSenderModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2019/2/18 11:40 AM
 */
public interface CallSenderDao extends BaseMapper<CallSenderModel> {
    List<CallSenderModel> selectStartCallSender();

}
