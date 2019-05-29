package org.songbai.loan.admin.chase.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.chase.po.ChaseFeedPo;
import org.songbai.loan.admin.chase.vo.ChaseFeedVo;
import org.songbai.loan.model.chase.ChaseFeedModel;

import java.util.List;

public interface ChaseFeedDao extends BaseMapper<ChaseFeedModel> {
    Integer queryFeedCount(@Param("po") ChaseFeedPo po,@Param("deptIds") List<Integer> deptIds);

    List<ChaseFeedVo> findChaseFeedList(@Param("po") ChaseFeedPo po,@Param("deptIds") List<Integer> deptIds);

    List<ChaseFeedVo> findChaseListByChaseId(@Param("chaseId") String chaseId, @Param("agencyId") Integer agencyId);
}
