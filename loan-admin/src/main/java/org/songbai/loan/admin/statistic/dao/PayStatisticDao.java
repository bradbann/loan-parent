package org.songbai.loan.admin.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.vo.StatisticPayVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.statistic.PayStatisticModel;

import java.util.List;

public interface PayStatisticDao extends BaseMapper<PayStatisticModel> {


    int findStatisticPaymentCount(@Param("po") StatisticPayPO po);

    List<StatisticPayVO> findStatisticPaymentList(@Param("po") StatisticPayPO po, @Param("page") PageRow pageRow);

    PayStatisticModel findStatisticPaymenByAngecyIdAndDate(@Param("agencyId") Integer agencyId, @Param("date") String date);

}
