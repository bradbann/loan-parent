package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.statistic.UserStatisticModel;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;

/**
 * Author: qmw
 * Date: 2018/11/28 4:49 PM
 */
public interface UserActionStatisticDao extends BaseMapper<UserStatisticModel> {


    void insertUserStatisticByAgencyIdAndActionDate(UserStatisticDTO dto);

    int updateUserStatisticByAgencyIdAndActionDate(UserStatisticDTO dto);
}
