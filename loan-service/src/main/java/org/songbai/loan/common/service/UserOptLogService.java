package org.songbai.loan.common.service;

import org.songbai.loan.constant.PlatformEnum;
import org.songbai.loan.constant.user.UserConstant;
import org.songbai.loan.model.user.UserOptLogModel;

import java.util.Date;
import java.util.List;

public interface UserOptLogService {
    void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, String beforeValue, String afterValue);

    void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType);

    void save(Integer userid, String ip, String browserAgent, UserConstant.Opt optType, PlatformEnum device);
    List<UserOptLogModel> queryList(Integer userId, UserConstant.Opt optType, Date endTime, int size);
}
