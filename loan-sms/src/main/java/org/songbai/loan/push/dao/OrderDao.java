package org.songbai.loan.push.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.OrderModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface OrderDao extends BaseMapper<OrderModel> {

    Set<String> findTodayRepayOrder(@Param("today") LocalDate today, @Param("vestId") Integer vestId, @Param(value = "scopces") List<String> scopces);

    Set<String> findOrderOverdueUserDeviceId(@Param("exceedDays") Integer exceedDays, @Param("exceedFee") Double exceedFee,
                                             @Param("vestId") Integer vestId, @Param(value = "scopces") List<String> scopces);

    Set<String> findCallTodayRepayOrder(@Param("today") LocalDate today, @Param("vestId") Integer vestId,@Param("page") PageRow pageRow);

}
