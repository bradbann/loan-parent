package org.songbai.loan.admin.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.statistic.PayStatisticModel;
import org.songbai.loan.model.statistic.RepayStatisticModel;

import java.util.List;

public interface RepayStatisticDao extends BaseMapper<RepayStatisticModel> {


    int findStatisticRepaymentCount(@Param("po") StatisticPayPO po);

    List<RepayStatisticModel> findStatisticRepaymentList(@Param("po") StatisticPayPO po, @Param("page") PageRow pageRow);

    RepayStatisticModel findStatisticRepaymenByAngecyIdAndDate(@Param("agencyId") Integer agencyId, @Param("date") String date);
}
