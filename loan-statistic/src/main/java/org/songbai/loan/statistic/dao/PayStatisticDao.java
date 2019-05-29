package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.statistic.PayStatisticModel;
import org.songbai.loan.model.statistic.dto.PayStatisticDTO;

public interface PayStatisticDao extends BaseMapper<PayStatisticModel> {


    Integer updatePayStatisticByAgencyIdAndPayDate(PayStatisticDTO dto);


    void insertPayStatisticByAgencyIdAndPayDate(PayStatisticDTO dto);


}
