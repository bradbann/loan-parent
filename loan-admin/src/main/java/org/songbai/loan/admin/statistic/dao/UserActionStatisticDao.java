package org.songbai.loan.admin.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.songbai.loan.admin.statistic.model.vo.StatisHomeVO;
import org.songbai.loan.admin.statistic.model.vo.StatisticUserVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.statistic.UserStatisticModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/28 4:49 PM
 */
public interface UserActionStatisticDao extends BaseMapper<UserStatisticModel> {

    int findStatisticUserCount(@Param("po") StatisticUserPO po);

    List<StatisticUserVO> findStatisticUserList(@Param("po") StatisticUserPO po, @Param("page") PageRow pageRow);

    StatisHomeVO findHomeUserStatistic(@Param("agencyId") Integer agencyId, @Param("date") String date);

}
