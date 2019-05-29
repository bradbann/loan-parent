package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.statistic.RepayStatisticModel;
import org.songbai.loan.model.statistic.dto.PayStatisticDTO;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;

import java.util.Date;

public interface RepayStatisticDao extends BaseMapper<RepayStatisticModel> {
    /**
     * 查询代理某个交易日的统计
     * @return
     */
    RepayStatisticModel findRepayStatisticByRepayDateAndAgencyId(@Param("repayDate") Date repayDate, @Param("agencyId") Integer agencyId);


    Integer updateRepayStatisticByAgencyIdAndPayDate(PayStatisticDTO dto);

    void insertRepayStatisticByAgencyIdAndPayDate(PayStatisticDTO dto);

    Integer updateRepayStatistic(RepayStatisticDTO dto);

    RepayStatisticModel selectt(RepayStatisticDTO dto);

}
