package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.statistic.UserStatisticModel;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;

/**
 * Author: qmw
 * Date: 2018/11/28 4:49 PM
 */
public interface UserStatisticDao extends BaseMapper<UserStatisticModel> {

    int updateUserStatisticByAgencyIdAndRegisterDate(UserStatisticDTO dto);

    void insertUserStatisticByAgencyIdAndRegisterDate(UserStatisticDTO dto);

}
